/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author lucasayala
 */

@Entity 
@Table(name = "plantas")
public class Planta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_planta")
    private Long idPlanta;
    
    // many plantas pueden pertenecer a un user
    @ManyToOne(fetch = FetchType.LAZY) // FetchType.LAZY para que no se cargue el user con la planta en la lista (optimizamos)
    @JoinColumn(name = "id_usuario", nullable = false) // FK en la tabla plantas
    private Usuario usuario; // obj usuario al que pertenece este obj planta
    
    @Column(name = "nombre_comun", nullable = false, length = 100)
    private String nombreComun; 
    
    @Column(name = "nombre_cientifico", length = 100)
    private String nombreCientifico;
    
    @Column(name = "ubicacion", length = 100)
    private String ubicacion; 
    
    @Column(name = "fecha:adquisicion")
    private LocalDate fechaAdquisicion; 
    
    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;
    
    @Column(name = "frecuencia_riego_dias")
    private Integer frecuenciaRiegoDias; // Integer para permitir null si no se especifica, al ser un objeto y no un dato primitivo.
    
    @Lob // Indica que el campo es un Large I¡Object. Usamos de esta manera ya que el desarrollo de la app es para fines educativos, ya en entornos de producción se usaría una nube para cargar las imagenes 
    @Column(name = "foto_planta")
    private byte[] fotoPlanta; // datatype para BLOBs
    
    @Column(name = "ultima_fecha_riego")
    private LocalDateTime ultimaFechaRiego; 
    
    // Relación 1 a muchos: Una planta puede tener muchos riegos
    @OneToMany(mappedBy = "planta", cascade = CascadeType.ALL, orphanRemoval = true)//orphan removal, si elimino la planta, se borran los riegos 
    private List<Riego> riegos; //Lista de riegos asociados a esta planta 
    
    // CONSTRUCTORES 
    
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
        this.riegos = riegos;        
    }
    
    // sin ID ni lista de riegos para la creación de nuevas plantas
    public Planta(Usuario usuario, String nombreComun, String nombreCientifico, String ubicacion, LocalDate fechaAdquisicion,String notas, Integer frecuenciaRiegoDias, byte[] fotoPlanta, LocalDateTime ultimaFechaRiego) {
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
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public String getNombreComun() {
        return nombreComun;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }

    public String getNotas() {
        return notas;
    }

    public Integer getFrecuenciaRiegoDias() {
        return frecuenciaRiegoDias;
    }

    public byte[] getFotoPlanta() {
        return fotoPlanta;
    }

    public LocalDateTime getUltimaFechaRiego() {
        return ultimaFechaRiego;
    }

    public List<Riego> getRiegos() {
        return riegos;
    }
    
    public void setIdPlanta(Long idPlanta) {
        this.idPlanta = idPlanta;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setNombreComun(String nombreComun) {
        this.nombreComun = nombreComun;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public void setFrecuenciaRiegoDias(Integer frecuenciaRiegoDias) {
        this.frecuenciaRiegoDias = frecuenciaRiegoDias;
    }

    public void setFotoPlanta(byte[] fotoPlanta) {
        this.fotoPlanta = fotoPlanta;
    }

    public void setUltimaFechaRiego(LocalDateTime ultimaFechaRiego) {
        this.ultimaFechaRiego = ultimaFechaRiego;
    }

    public void setRiegos(List<Riego> riegos) {
        this.riegos = riegos;
    }
     //el idPlanta es el que define la identidad del usuario, por eso se inserta equals() y hashCode() para ese campo
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.idPlanta);
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
        final Planta other = (Planta) obj;
        return Objects.equals(this.idPlanta, other.idPlanta);
    }
    
    @Override
    public String toString() {
        return "Planta{" +
               "idPlanta=" + idPlanta +
               ", nombreComun='" + nombreComun + '\'' +
               ", nombreCientifico='" + nombreCientifico + '\'' +
               ", ubicacion='" + ubicacion + '\'' +
               ", fechaAdquisicion=" + fechaAdquisicion +
               ", frecuenciaRiegoDias=" + frecuenciaRiegoDias +
               ", ultimaFechaRiego=" + ultimaFechaRiego +
               // No incluir el array de bytes de la foto para evitar logs muy largos
               '}';
    }    
}
