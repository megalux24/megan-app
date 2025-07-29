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

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Autowired
    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    // Método para crear y guardar una nueva notificación con una planta
    public Notificacion crearNotificacion(Usuario usuario, Planta planta, String texto) {
        Notificacion notificacion = new Notificacion(usuario, planta, texto, LocalDateTime.now(), false);
        return notificacionRepository.save(notificacion);
    }

    // Método para crear una notificación sin planta específica
    public Notificacion crearNotificacion(Usuario usuario, String texto) {
        Notificacion notificacion = new Notificacion(usuario, null, texto, LocalDateTime.now(), false);
        return notificacionRepository.save(notificacion);
    }

    // Método para obtener todas las notificaciones de la base de datos.
    public List<Notificacion> findAll() {
        return notificacionRepository.findAll();
    }

    // Obtener todas las notificaciones de un usuario
    public List<Notificacion> getNotificacionesByUsuario(Usuario usuario) {
        return notificacionRepository.findByUsuario(usuario);
    }

    // Obtener notificaciones no leídas de un usuario
    public List<Notificacion> getNotificacionesNoLeidasByUsuario(Usuario usuario) {
        return notificacionRepository.findByUsuarioAndLeidaFalse(usuario);
    }

    // Marcar una notificación como leída
    public Optional<Notificacion> marcarComoLeida(Long idNotificacion) {
        Optional<Notificacion> optionalNotificacion = notificacionRepository.findById(idNotificacion);
        if (optionalNotificacion.isPresent()) {
            Notificacion notificacion = optionalNotificacion.get();
            notificacion.setLeida(true);
            return Optional.of(notificacionRepository.save(notificacion));
        }
        return Optional.empty();
    }

    // Eliminar una notificación
    public void eliminarNotificacion(Long idNotificacion) {
        notificacionRepository.deleteById(idNotificacion);
    }
}
