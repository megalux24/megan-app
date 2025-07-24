/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
/**
 *
 * @author lucasayala
 */
@Entity
@Table(name = "riegos")
public class Riego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_riego")
    private Long idRiego;
    
    // Relación Many-to-One con Planta: Muchos riegos pueden pertenecer a una planta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_planta", nullable = false)
    private Planta planta; // Objeto Planta al que pertenece este riego
    
    @Column(name = "fecha_hora_riego", nullable = false)
    private LocalDateTime fechaHoraRiego;
    
    @Column(name = "cantidad_agua_ml", precision = 7, scale = 2)
    private BigDecimal cantidadAguaMl;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    public Riego() {        
    }
    
    public Riego(Long idRiego, Planta planta, LocalDateTime fechaHoraRiego, BigDecimal cantidadAguaMl, String observaciones) {
        this.idRiego = idRiego; 
        this.planta = planta;
        this.fechaHoraRiego = fechaHoraRiego;
        this.cantidadAguaMl = cantidadAguaMl;
        this.observaciones = observaciones;
    }
    
    public Riego(Planta planta, LocalDateTime fechaHoraRiego, BigDecimal cantidadAguaMl, String observaciones) {
        this.planta = planta;
        this.fechaHoraRiego = fechaHoraRiego;
        this.cantidadAguaMl = cantidadAguaMl;
        this.observaciones = observaciones;
    }

    public Long getIdRiego() {
        return idRiego;
    }

    public Planta getPlanta() {
        return planta;
    }

    public LocalDateTime getFechaHoraRiego() {
        return fechaHoraRiego;
    }

    public BigDecimal getCantidadAguaMl() {
        return cantidadAguaMl;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setIdRiego(Long idRiego) {
        this.idRiego = idRiego;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public void setFechaHoraRiego(LocalDateTime fechaHoraRiego) {
        this.fechaHoraRiego = fechaHoraRiego;
    }

    public void setCantidadAguaMl(BigDecimal cantidadAguaMl) {
        this.cantidadAguaMl = cantidadAguaMl;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.idRiego);
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
        final Riego other = (Riego) obj;
        return Objects.equals(this.idRiego, other.idRiego);
    }
    
    @Override
    public String toString() {
        return "Riego{" +
               "idRiego=" + idRiego +
               ", fechaHoraRiego=" + fechaHoraRiego +
               ", cantidadAguaMl=" + cantidadAguaMl +
               '}';
    }
}
