/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.service;

import com.megan.model.Notificacion; // Importar Notificacion
import com.megan.model.Planta;
import com.megan.model.Usuario;
import com.megan.model.repository.PlantaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author lucasayala
 */
@Service
public class PlantaService {
    private final PlantaRepository plantaRepository;
    private final NotificacionService notificacionService;
    
    @Autowired
    public PlantaService(PlantaRepository plantaRepository, NotificacionService notificacionService) {
        this.plantaRepository = plantaRepository;
        this.notificacionService = notificacionService; // Asignar NotificacionService
    }
    
    // Método para crear una nueva planta
    public Planta crearPlanta(Planta planta) {
        Planta nuevaPlanta = plantaRepository.save(planta);
        // Notificar al usuario que la planta ha sido creada
        notificacionService.crearNotificacion(nuevaPlanta.getUsuario(), nuevaPlanta, "¡Tu planta " + nuevaPlanta.getNombreComun() + " ha sido registrada!");
        return nuevaPlanta;
    }
    
    public Optional<Planta> getPlantaById(Long id) {
        return plantaRepository.findById(id);
    }
    
    public List<Planta> getPlantasByUsuario(Usuario usuario) {
        List<Planta> plantas = plantaRepository.findByUsuario(usuario);
        // Acá se puede generar notificaciones si las plantas necesitan chequeo
        for (Planta planta : plantas) {
            if (necesitaChequeoRiego(planta)) {
                // Este bucle permite procesar individualmente cada planta que el usuario tiene, para poder aplicar la lógica de if 
                //(necesitaChequeoRiego(planta)) y, si es necesario, crear una notificación para esa planta específica
                String textoNotificacion = "¡Es hora de chequear el riego de tu planta " + planta.getNombreComun() + "!";
                notificacionService.crearNotificacion(planta.getUsuario(), planta, textoNotificacion);
            }
        }
        return plantas;
    }
    
    // Método para actualizar una planta
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
            plantaExistente.setFotoPlanta(plantaDetails.getFotoPlanta()); // Actualizar la foto
            return plantaRepository.save(plantaExistente);
        }
        return null; // O lanzar una excepción
    }
    
    // Método para eliminar una planta
    public void deletePlanta(Long id) {
        plantaRepository.deleteById(id);
    }
    
    // Lógica para determinar si una planta necesita ser chequeada para riego
    public boolean necesitaChequeoRiego(Planta planta) {
        // Si no hay última fecha de riego o frecuencia, no se puede determinar
        if (planta.getUltimaFechaRiego() == null || planta.getFrecuenciaRiegoDias() == null || planta.getFrecuenciaRiegoDias() <= 0) {
            return false;
        }
        // Calcular la fecha estimada para el próximo chequeo
        LocalDateTime proximoChequeoEstimado = planta.getUltimaFechaRiego().plusDays(planta.getFrecuenciaRiegoDias());
        // Si la fecha actual es posterior a la fecha estimada de chequeo, necesita ser chequeada
        return LocalDateTime.now().isAfter(proximoChequeoEstimado);
    }
}
