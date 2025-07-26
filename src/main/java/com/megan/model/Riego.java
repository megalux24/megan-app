// src/main/java/com/megan/model/Riego.java
package com.megan.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore; // Importar JsonIgnore

@Entity
@Table(name = "riegos")
public class Riego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_riego")
    private Long idRiego;

    // IGNORAR la Planta al serializar Riego para evitar recursión
    @JsonIgnore // <--- AÑADIR ESTA ANOTACIÓN
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_planta", nullable = false)
    private Planta planta;

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

    public void setIdRiego(Long idRiego) {
        this.idRiego = idRiego;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public LocalDateTime getFechaHoraRiego() {
        return fechaHoraRiego;
    }

    public void setFechaHoraRiego(LocalDateTime fechaHoraRiego) {
        this.fechaHoraRiego = fechaHoraRiego;
    }

    public BigDecimal getCantidadAguaMl() {
        return cantidadAguaMl;
    }

    public void setCantidadAguaMl(BigDecimal cantidadAguaMl) {
        this.cantidadAguaMl = cantidadAguaMl;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Riego riego = (Riego) o;
        return Objects.equals(idRiego, riego.idRiego);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRiego);
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
