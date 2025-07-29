package com.megan.controller;

/**
 *
 * @author lucasayala
 */
/* Esta clase es un objeto simple para recibir los datos del frontend que soluciona un problema con el registro de riego
 Al intentar registrar un riego, Spring no traducía correctamente el objeto anidado que envía el FE
*/
public class RiegoRequestDTO {
    private Long plantaId;
    private Double cantidadAguaMl;
    private String observaciones;

    // Getters y Setters
    public Long getPlantaId() {
        return plantaId;
    }

    public void setPlantaId(Long plantaId) {
        this.plantaId = plantaId;
    }

    public Double getCantidadAguaMl() {
        return cantidadAguaMl;
    }

    public void setCantidadAguaMl(Double cantidadAguaMl) {
        this.cantidadAguaMl = cantidadAguaMl;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}

    

