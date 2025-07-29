// src/main/java/com/megan/model/Planta.java
package com.megan.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonBackReference; // Importar JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore; // Importar JsonIgnore

@Entity
@Table(name = "plantas")
public class Planta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planta")
    private Long idPlanta;

    // Relación Many-to-One con Usuario: Muchas plantas pueden pertenecer a un usuario
    // @JsonBackReference indica que esta es la parte "de vuelta" de la relación
    // y se ignora durante la serialización para evitar recursión, pero se deserializa.
    @JsonBackReference("usuario-plantas")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre_comun", nullable = false, length = 100)
    private String nombreComun;

    @Column(name = "nombre_cientifico", length = 100)
    private String nombreCientifico;

    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    @Column(name = "fecha_adquisicion")
    private LocalDate fechaAdquisicion;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "frecuencia_riego_dias")
    private Integer frecuenciaRiegoDias;

    @Column(name = "foto_planta", columnDefinition = "LONGBLOB") 
    private byte[] fotoPlanta;

    @Column(name = "ultima_fecha_riego")
    private LocalDateTime ultimaFechaRiego;
    
    // IGNORAR los Riegos al serializar Planta para evitar recursión
    @JsonIgnore 
    @OneToMany(mappedBy = "planta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Riego> riegos;
    
    // IGNORAR las Notificaciones al serializar Planta para evitar recursión
    @JsonIgnore 
    @OneToMany(mappedBy = "planta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacion> notificaciones;

    public Planta() {
    }

    public Planta(Long idPlanta, Usuario usuario, String nombreComun, String nombreCientifico, String ubicacion, LocalDate fechaAdquisicion, String notas, Integer frecuenciaRiegoDias, byte[] fotoPlanta, LocalDateTime ultimaFechaRiego, List<Riego> riegos) {
        this.idPlanta = idPlanta;
        this.usuario = usuario;
        this.nombreComun = nombreComun;
        this.nombreCientifico = nombreCientifico;
        this.ubicacion = ubicacion;
        this.fechaAdquisicion = fechaAdquisicion;
        this.notas = notas;
        this.frecuenciaRiegoDias = frecuenciaRiegoDias;
        this.fotoPlanta = fotoPlanta;
        this.ultimaFechaRiego = ultimaFechaRiego;
        this.riegos = riegos;
    }

    public Planta(Usuario usuario, String nombreComun, String nombreCientifico, String ubicacion, LocalDate fechaAdquisicion, String notas, Integer frecuenciaRiegoDias, byte[] fotoPlanta, LocalDateTime ultimaFechaRiego) {
        this.usuario = usuario;
        this.nombreComun = nombreComun;
        this.nombreCientifico = nombreCientifico;
        this.ubicacion = ubicacion;
        this.fechaAdquisicion = fechaAdquisicion;
        this.notas = notas;
        this.frecuenciaRiegoDias = frecuenciaRiegoDias;
        this.fotoPlanta = fotoPlanta;
        this.ultimaFechaRiego = ultimaFechaRiego;
    }

    public Long getIdPlanta() {
        return idPlanta;
    }

    public void setIdPlanta(Long idPlanta) {
        this.idPlanta = idPlanta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombreComun() {
        return nombreComun;
    }

    public void setNombreComun(String nombreComun) {
        this.nombreComun = nombreComun;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Integer getFrecuenciaRiegoDias() {
        return frecuenciaRiegoDias;
    }

    public void setFrecuenciaRiegoDias(Integer frecuenciaRiegoDias) {
        this.frecuenciaRiegoDias = frecuenciaRiegoDias;
    }

    public byte[] getFotoPlanta() {
        return fotoPlanta;
    }

    public void setFotoPlanta(byte[] fotoPlanta) {
        this.fotoPlanta = fotoPlanta;
    }

    public LocalDateTime getUltimaFechaRiego() {
        return ultimaFechaRiego;
    }

    public void setUltimaFechaRiego(LocalDateTime ultimaFechaRiego) {
        this.ultimaFechaRiego = ultimaFechaRiego;
    }

    public List<Riego> getRiegos() {
        return riegos;
    }

    public void setRiegos(List<Riego> riegos) {
        this.riegos = riegos;
    }
    
    public List<Notificacion> getNotificaciones() { 
        return notificaciones; 
    }
    public void setNotificaciones(List<Notificacion> notificaciones) { 
        this.notificaciones = notificaciones; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Planta planta = (Planta) o;
        return Objects.equals(idPlanta, planta.idPlanta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlanta);
    }

    @Override
    public String toString() {
        return "Planta{" +
               "idPlanta=" + idPlanta +
               ", nombreComun='" + nombreComun + '\'' +
               ", nombreCientifico='" + nombreCientifico + '\'' +
               ", ubicacion='" + ubicacion + '\'' +
               ", fechaAdquisicion=" + fechaAdquisicion +
               ", notas='" + notas + '\'' +
               ", frecuenciaRiegoDias=" + frecuenciaRiegoDias +
               ", ultimaFechaRiego=" + ultimaFechaRiego +
               '}';
    }
}
