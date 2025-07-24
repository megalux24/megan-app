/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 *
 * @author lucasayala
 */
@Entity
@Table(name = "notificaciones")
public class Notificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;
    
    // Relación muchos a uno con Usuario: Una notificación pertenece a un usuario
    @ManyToOne(fetch = FetchType.LAZY)// para que cuándo se cargue la notificación, no deba recuperar el Usuario also. (mejora el rendimiento) 
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario; // Objeto Usuario al que pertenece esta notificación
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_planta")
    private Planta planta;
    
    @Column(name = "texto_notificacion", nullable = false, length = 500)
    private String textoNotificacion;
    
    @Column(name = "fecha_notificacion", nullable = false)
    private LocalDateTime fechaNotificacion;
    
    @Column(name = "leida", nullable = false)
    private Boolean leida;
    
    public Notificacion() {    
    }
    
    public Notificacion(Long idNotificacion, Usuario usuario, Planta planta, String textoNotificacion, LocalDateTime fechaNotificacion, Boolean leida) {
        this.idNotificacion = idNotificacion;
        this.usuario = usuario;
        this.planta = planta;
        this.textoNotificacion = textoNotificacion;
        this.fechaNotificacion = fechaNotificacion;
        this.leida = leida;
    }
    
    public Notificacion(Usuario usuario, Planta planta, String textoNotificacion, LocalDateTime fechaNotificacion, Boolean leida) {
        this.usuario = usuario;
        this.planta = planta;
        this.textoNotificacion = textoNotificacion;
        this.fechaNotificacion = fechaNotificacion;
        this.leida = leida;
    }

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Planta getPlanta() {
        return planta;
    }

    public String getTextoNotificacion() {
        return textoNotificacion;
    }

    public LocalDateTime getFechaNotificacion() {
        return fechaNotificacion;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setIdNotificacion(Long idNotificacion) {
        this.idNotificacion = idNotificacion;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public void setTextoNotificacion(String textoNotificacion) {
        this.textoNotificacion = textoNotificacion;
    }

    public void setFechaNotificacion(LocalDateTime fechaNotificacion) {
        this.fechaNotificacion = fechaNotificacion;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.idNotificacion);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Notificacion other = (Notificacion) obj;
        return Objects.equals(this.idNotificacion, other.idNotificacion);
    }
    
    @Override
    public String toString() {
        return "Notificacion{" +
               "idNotificacion=" + idNotificacion +
               ", textoNotificacion='" + textoNotificacion + '\'' +
               ", fechaNotificacion=" + fechaNotificacion +
               ", leida=" + leida +
               '}';
    }
    
}