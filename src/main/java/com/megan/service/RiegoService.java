/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.service;

import com.megan.model.Notificacion; 
import com.megan.model.Planta;
import com.megan.model.Riego;
import com.megan.model.repository.PlantaRepository;
import com.megan.model.repository.RiegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author lucasayala
 */
@Service
public class RiegoService {
    // inyectamos dependencias y otros componentes necesarios para realizar tareas
    private final RiegoRepository riegoRepository; //para interactuar con la tabla riegos 
    private final PlantaRepository plantaRepository; // para interactuar con la tabla plantas
    private final NotificacionService notificacionService; // para crear y gestionar las notif. relacionadas con los riegos 
    
    //constructor fundamental para que Spring pueda proporcionarle a RiegoService todas las dependencias que necesita para funcionar correctamente.
    @Autowired //"autocableado", la magia de Spring que automatiza la conexión con otros componentes del programa
    public RiegoService(RiegoRepository riegoRepository, PlantaRepository plantaRepository, NotificacionService notificacionService) {
        this.riegoRepository = riegoRepository;
        this.plantaRepository = plantaRepository;
        this.notificacionService = notificacionService; 
    }
    
    // Método para registrar un nuevo riego
    public Riego registrarRiego(Riego riego) {
        riego.setFechaHoraRiego(LocalDateTime.now()); //que la FechaHoraRiego sea la actual!
        
            // Actualizar la ultimaFechaRiego de la planta asociada
        Optional<Planta> optionalPlanta = plantaRepository.findById(riego.getPlanta().getIdPlanta());
        if (optionalPlanta.isPresent()) {
            Planta planta = optionalPlanta.get();
            planta.setUltimaFechaRiego(riego.getFechaHoraRiego()); // Establecer la fecha del riego actual, que usamos LocalDateTime.now()
            plantaRepository.save(planta); // Guardar la planta actualizada

            // Generar una notificación de confirmación de riego
            String textoNotificacion = "¡Riego registrado para tu planta " + planta.getNombreComun() + "!";
            notificacionService.crearNotificacion(planta.getUsuario(), planta, textoNotificacion);

        } else {
            System.err.println("Error: Planta con ID " + riego.getPlanta().getIdPlanta() + " no encontrada para registrar riego.");
            
        }
        return riegoRepository.save(riego);
    }    

        
        
    // Método para obtener un riego por su ID
    public Optional<Riego> getRiegoById(Long id) {
        return riegoRepository.findById(id);
    }

    // Método para obtener todos los riegos de una planta específica
    public List<Riego> getRiegosByPlanta(Planta planta) {
        return riegoRepository.findByPlanta(planta);
    }

    // Método para eliminar un riego
    public void deleteRiego(Long id) {
        riegoRepository.deleteById(id);
    }
}
    

