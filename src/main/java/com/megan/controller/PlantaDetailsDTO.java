package com.megan.controller; // o com.megan.dto

import com.megan.model.Planta;
import com.megan.model.Riego;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Este DTO combina datos de Planta y su último Riego para mostrar las observaciones creadas durante el registro de un riego 
public class PlantaDetailsDTO {

    // Campos de la Planta
    private String nombreComun;
    private String nombreCientifico;
    private String ubicacion;
    private LocalDate fechaAdquisicion;
    private String notas;
    private Integer frecuenciaRiegoDias;

    // Campos del último Riego
    private LocalDateTime ultimaFechaRiego;
    private String ultimasObservacionesRiego;

    // Constructor que facilita la creación del DTO
    public PlantaDetailsDTO(Planta planta, Riego ultimoRiego) {
        this.nombreComun = planta.getNombreComun();
        this.nombreCientifico = planta.getNombreCientifico();
        this.ubicacion = planta.getUbicacion();
        this.fechaAdquisicion = planta.getFechaAdquisicion();
        this.notas = planta.getNotas();
        this.frecuenciaRiegoDias = planta.getFrecuenciaRiegoDias();
        this.ultimaFechaRiego = planta.getUltimaFechaRiego();

        // Si existe un último riego, añadimos sus observaciones
        if (ultimoRiego != null) {
            this.ultimasObservacionesRiego = ultimoRiego.getObservaciones();
        } else {
            this.ultimasObservacionesRiego = "No hay observaciones de riego todavía.";
        }
    }

    // Getters
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
    public LocalDateTime getUltimaFechaRiego() {
        return ultimaFechaRiego; 
    }
    public String getUltimasObservacionesRiego() {
        return ultimasObservacionesRiego;
    }
}
