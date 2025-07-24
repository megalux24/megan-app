/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.service;

import com.megan.model.Notificacion;
import com.megan.model.Planta;
import com.megan.model.Usuario;
import com.megan.model.repository.NotificacionRepository;
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
public class NotificacionService {
    
    private final NotificacionRepository notificacionRepository;
    
    @Autowired
    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }
    
    // Método para crear y guardar una nueva notificación
    public Notificacion crearNotificacion(Usuario usuario, Planta planta, String texto) {
        Notificacion notificacion = new Notificacion(usuario, planta, texto, LocalDateTime.now(), false);
        return notificacionRepository.save(notificacion);
    }
    
    // Método para crear una notifiación sin planta específica
    public Notificacion crearNotificacion(Usuario usuario, String texto) {
        Notificacion notificacion = new Notificacion(usuario, null, texto, LocalDateTime.now(), false);
        return notificacionRepository.save(notificacion);
    }
    
    public List<Notificacion> getNotificacionesByUsuario(Usuario usuario) {
        return notificacionRepository.findByUsuario(usuario);
    }
    // En este método, con el AndLeidaFalse, el and añade la condición de que el campo Leida sea false
    // Spring Data JPA es una abstracción que se encarga de generar la consulta SQL con esa condición
    public List<Notificacion> getNotificacionesNoLeidasByUsuario(Usuario usuario) {
        return notificacionRepository.findByUsuarioAndLeidaFalse(usuario);
    }
    
    //busca una notificación por su ID, si la encuentra, actualiza su estado a "leída" en la base de datos 
    //y devuelve la notificación actualizada. Si no la encuentra, devuelve un Optional vacío.
    public Optional<Notificacion> marcarComoLeida(Long idNotificacion) {
        Optional<Notificacion> optionalNotificacion = notificacionRepository.findById(idNotificacion);
        if (optionalNotificacion.isPresent()) {
            Notificacion notificacion = optionalNotificacion.get();
            notificacion.setLeida(true);
            return Optional.of(notificacionRepository.save(notificacion));
        }
        return Optional.empty();                
    }
    
    public void eliminarNotificacion(Long idNotificacion) {
        notificacionRepository.deleteById(idNotificacion);
    }
        
}
