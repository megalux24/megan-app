package com.megan.service;

import com.megan.controller.RiegoRequestDTO;
import com.megan.model.Notificacion;
import com.megan.model.Planta;
import com.megan.model.Riego;
import com.megan.model.repository.PlantaRepository;
import com.megan.model.repository.RiegoRepository;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RiegoService {
    private final RiegoRepository riegoRepository;
    private final PlantaRepository plantaRepository;
    private final NotificacionService notificacionService;

    @Autowired
    public RiegoService(RiegoRepository riegoRepository, PlantaRepository plantaRepository, NotificacionService notificacionService) {
        this.riegoRepository = riegoRepository;
        this.plantaRepository = plantaRepository;
        this.notificacionService = notificacionService;
    }
    
    public Riego registrarRiego(RiegoRequestDTO riegoRequest) {
        // Buscamos la planta usando el ID del DTO
        Planta planta = plantaRepository.findById(riegoRequest.getPlantaId())
                .orElseThrow(() -> new IllegalArgumentException("Planta no encontrada con ID: " + riegoRequest.getPlantaId()));

        // Creamos el nuevo objeto Riego
        Riego nuevoRiego = new Riego();
        nuevoRiego.setPlanta(planta);
        nuevoRiego.setFechaHoraRiego(LocalDateTime.now());
        nuevoRiego.setObservaciones(riegoRequest.getObservaciones());
        if (riegoRequest.getCantidadAguaMl() != null) {
            nuevoRiego.setCantidadAguaMl(BigDecimal.valueOf(riegoRequest.getCantidadAguaMl()));
        }
        
        // Actualizamos la última fecha de riego en la planta
        planta.setUltimaFechaRiego(nuevoRiego.getFechaHoraRiego());
        plantaRepository.save(planta);

        // Generamos la notificación
        String textoNotificacion = "Riego registrado para tu planta " + planta.getNombreComun();
        notificacionService.crearNotificacion(planta.getUsuario(), planta, textoNotificacion);

        // Guardamos y devolvemos el nuevo riego
        return riegoRepository.save(nuevoRiego);
    }    
    
    public List<Riego> getRiegosByPlantaId(Long plantaId) {
        return plantaRepository.findById(plantaId)
                .map(riegoRepository::findByPlanta)
                .orElse(Collections.emptyList());
    }

    public Optional<Riego> getRiegoById(Long id) {
        return riegoRepository.findById(id);
    }

    public List<Riego> getRiegosByPlanta(Planta planta) {
        return riegoRepository.findByPlanta(planta);
    }

    public void deleteRiego(Long id) {
        riegoRepository.deleteById(id);
    }
}
