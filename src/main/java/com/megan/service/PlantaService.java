package com.megan.service;

import com.megan.model.Notificacion;
import com.megan.model.Planta;
import com.megan.model.Usuario;
import com.megan.model.repository.NotificacionRepository;
import com.megan.model.repository.PlantaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.megan.controller.PlantaDetailsDTO;
import com.megan.model.Riego;
import com.megan.model.repository.RiegoRepository;

@Service
public class PlantaService {

    private final PlantaRepository plantaRepository;
    private final NotificacionService notificacionService;
    private final NotificacionRepository notificacionRepository;
    private final RiegoRepository riegoRepository;

    @Autowired
    public PlantaService(PlantaRepository plantaRepository, NotificacionService notificacionService, NotificacionRepository notificacionRepository, RiegoRepository riegoRepository) {
        this.plantaRepository = plantaRepository;
        this.notificacionService = notificacionService;
        this.notificacionRepository = notificacionRepository;
        this.riegoRepository = riegoRepository;
    }

    public Planta crearPlanta(Planta planta) {
        return plantaRepository.save(planta);
    }

    public Optional<Planta> getPlantaById(Long id) {
        return plantaRepository.findById(id);
    }

    // --- MÉTODO CORREGIDO QUE EVITA EL StackOverflowError ---
    public List<Planta> getPlantasByUsuario(Usuario usuario) {
        List<Planta> plantas = plantaRepository.findByUsuario(usuario);
        
        /*
        La lógica para crear notificaciones se ha comentado porque causaba la recursión infinita.
        Una petición GET (obtener datos) nunca debe modificar datos en el servidor.
        Esta funcionalidad debería implementarse de otra forma, como una tarea programada.
        */
        
        return plantas;
    }
    
    public Optional<PlantaDetailsDTO> getPlantaDetailsById(Long id) {
        // Buscamos la planta
        Optional<Planta> plantaOpt = plantaRepository.findById(id);

        if (plantaOpt.isEmpty()) {
            return Optional.empty();
        }

        Planta planta = plantaOpt.get();
        // Buscamos su último riego usando el nuevo método del repositorio
        Riego ultimoRiego = riegoRepository.findTopByPlantaOrderByFechaHoraRiegoDesc(planta);

        // Creamos y devolvemos el DTO con toda la información
        PlantaDetailsDTO detailsDTO = new PlantaDetailsDTO(planta, ultimoRiego);
        return Optional.of(detailsDTO);
    }


    public Planta updatePlanta(Long id, Planta plantaDetails) {
        Optional<Planta> optionalPlanta = plantaRepository.findById(id);
        if (optionalPlanta.isPresent()) {
            Planta plantaExistente = optionalPlanta.get();
            plantaExistente.setNombreComun(plantaDetails.getNombreComun());
            plantaExistente.setNombreCientifico(plantaDetails.getNombreCientifico());
            plantaExistente.setUbicacion(plantaDetails.getUbicacion());
            plantaExistente.setFechaAdquisicion(plantaDetails.getFechaAdquisicion());
            plantaExistente.setNotas(plantaDetails.getNotas());
            plantaExistente.setFrecuenciaRiegoDias(plantaDetails.getFrecuenciaRiegoDias());
            if (plantaDetails.getFotoPlanta() != null) {
                plantaExistente.setFotoPlanta(plantaDetails.getFotoPlanta());
            }
            return plantaRepository.save(plantaExistente);
        }
        return null;
    }

    @Transactional
    public void deletePlanta(Long id) {
        Optional<Planta> optionalPlanta = plantaRepository.findById(id);
        if (optionalPlanta.isPresent()) {
            Planta planta = optionalPlanta.get();
            List<Notificacion> notificacionesAsociadas = notificacionRepository.findByPlanta(planta);
            notificacionRepository.deleteAllInBatch(notificacionesAsociadas);
            plantaRepository.delete(planta);
        } else {
            throw new IllegalArgumentException("Planta con ID " + id + " no encontrada para eliminar.");
        }
    }

    public boolean necesitaChequeoRiego(Planta planta) {
        if (planta.getUltimaFechaRiego() == null || planta.getFrecuenciaRiegoDias() == null || planta.getFrecuenciaRiegoDias() <= 0) {
            return false;
        }
        LocalDateTime proximoChequeoEstimado = planta.getUltimaFechaRiego().plusDays(planta.getFrecuenciaRiegoDias());
        return LocalDateTime.now().isAfter(proximoChequeoEstimado);
    }
}