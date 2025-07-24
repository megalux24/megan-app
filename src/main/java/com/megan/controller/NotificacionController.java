/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.controller;

import com.megan.model.Notificacion;
import com.megan.model.Usuario; //para obtener el usuario
import com.megan.service.NotificacionService;
import com.megan.service.UsuarioService; //para obtener el usuario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author lucasayala
 */
@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {
    
    private final NotificacionService notificacionService;
    private final UsuarioService usuarioService; // Para obtener el usuario asociado a las notificaciones

    @Autowired
    public NotificacionController(NotificacionService notificacionService, UsuarioService usuarioService) {
        this.notificacionService = notificacionService;
        this.usuarioService = usuarioService;
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
        return new ResponseEntity<>(notificaciones, HttpStatus.OK);
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
        return new ResponseEntity<>(notificaciones, HttpStatus.OK);
    }
    
    // Endpoint para marcar una notificación como leída
    // PUT /api/notificaciones/{id}/marcar-leida
    @PutMapping("/{id}/marcar-leida")
    public ResponseEntity<Notificacion> marcarNotificacionComoLeida(@PathVariable Long id) {
        Optional<Notificacion> updatedNotificacion = notificacionService.marcarComoLeida(id);
        return updatedNotificacion.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                                  .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificacion(@PathVariable Long id) {
        notificacionService.eliminarNotificacion(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}
