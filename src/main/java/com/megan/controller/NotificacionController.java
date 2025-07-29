package com.megan.controller;

import com.megan.model.Notificacion;
import com.megan.model.Usuario;
import com.megan.service.NotificacionService;
import com.megan.service.UsuarioService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;
    @Autowired
    private UsuarioService usuarioService; // Inyectar UsuarioService

    // Usamos @Transactional para mantener la sesión de Hibernate abierta
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<Notificacion>> getAllNotificaciones() {
        try {
            List<Notificacion> notificaciones = notificacionService.findAll();
            // Forzar la inicialización de las relaciones 'lazy'
            notificaciones.forEach(notif -> { // Corregido 'notificacion' a 'notif' para consistencia
                if (notif.getPlanta() != null) {
                    Hibernate.initialize(notif.getPlanta());
                }
            });
            return ResponseEntity.ok(notificaciones);
        } catch (Exception e) {
            System.err.println("Error al obtener notificaciones: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoint para obtener todas las notificaciones de un usuario
    // GET /api/notificaciones/usuario/{userId}
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Notificacion>> getNotificacionesByUsuario(@PathVariable Long userId) {
        Optional<Usuario> optionalUsuario = usuarioService.getUsuarioById(userId);
        if (optionalUsuario.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Notificacion> notificaciones = notificacionService.getNotificacionesByUsuario(optionalUsuario.get());
        
        // Workaround para LazyInitializationException:
        List<Notificacion> initializedNotificaciones = notificaciones.stream().map(notif -> {
            if (notif.getUsuario() != null) {
                Hibernate.initialize(notif.getUsuario());
            }
            if (notif.getPlanta() != null) {
                Hibernate.initialize(notif.getPlanta());
            }
            return notif;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(initializedNotificaciones, HttpStatus.OK);
    }

    // Endpoint para obtener las notificaciones NO leídas de un usuario
    // GET /api/notificaciones/usuario/{userId}/no-leidas
    @GetMapping("/usuario/{userId}/no-leidas")
    public ResponseEntity<List<Notificacion>> getNotificacionesNoLeidasByUsuario(@PathVariable Long userId) {
        Optional<Usuario> optionalUsuario = usuarioService.getUsuarioById(userId);
        if (optionalUsuario.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Notificacion> notificaciones = notificacionService.getNotificacionesNoLeidasByUsuario(optionalUsuario.get());

        List<Notificacion> initializedNotificaciones = notificaciones.stream().map(notif -> {
            if (notif.getUsuario() != null) {
                Hibernate.initialize(notif.getUsuario());
            }
            if (notif.getPlanta() != null) {
                Hibernate.initialize(notif.getPlanta());
            }
            return notif;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(initializedNotificaciones, HttpStatus.OK);
    }

    // Endpoint para marcar una notificación como leída
    // PUT /api/notificaciones/{id}/marcar-leida
    @PutMapping("/{id}/marcar-leida")
    public ResponseEntity<Notificacion> marcarNotificacionComoLeida(@PathVariable Long id) {
        Optional<Notificacion> updatedNotificacion = notificacionService.marcarComoLeida(id);
        return updatedNotificacion.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                                  .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint para eliminar una notificación
    // DELETE /api/notificaciones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificacion(@PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
