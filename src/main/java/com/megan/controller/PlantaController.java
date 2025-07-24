/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.controller;

import com.fasterxml.jackson.databind.ObjectMapper; //para convertir JSON String a objeto
import com.megan.model.Planta;
import com.megan.model.Usuario; //para asociar la planta al usuario
import com.megan.service.PlantaService;
import com.megan.service.UsuarioService; //para obtener el usuario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // Para especificar el tipo de contenido
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Para manejar la carga de archivos

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author lucasayala
 */
@RestController
@RequestMapping("/api/plantas")
public class PlantaController {
    private final PlantaService plantaService;
    private final UsuarioService usuarioService; // p/obtener el usuario asociado a la planta
    private final ObjectMapper objectMapper; // convertir JSON a objeto
    
    @Autowired
    public PlantaController(PlantaService plantaService, UsuarioService usuarioService, ObjectMapper objectMapper) {
        this.plantaService = plantaService;
        this.usuarioService = usuarioService;
        this.objectMapper = objectMapper;
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Planta> crearPlanta(
            @RequestPart("planta") String plantaJson, // data de la planta como JSON String
            @RequestPart(value = "foto", required = false) MultipartFile foto) { // Archivo de la foto (opcional)
        try {
            Planta planta = objectMapper.readValue(plantaJson, Planta.class); // Convertir JSON String a objeto Planta

            // Asignar el usuario a la planta (asumiendo que el ID de usuario viene en el JSON de la planta)
            // En un sistema real, el usuario se obtendría del contexto de seguridad (ej. Spring Security)
            if (planta.getUsuario() == null || planta.getUsuario().getIdUsuario() == null) {
                return new ResponseEntity("El ID de usuario es requerido para crear una planta.", HttpStatus.BAD_REQUEST);
            }
            Optional<Usuario> optionalUsuario = usuarioService.getUsuarioById(planta.getUsuario().getIdUsuario());
            if (optionalUsuario.isEmpty()) {
                return new ResponseEntity("Usuario no encontrado.", HttpStatus.NOT_FOUND);
            }
            planta.setUsuario(optionalUsuario.get());

            // Si se proporciona una foto, convertirla a byte[] y asignarla
            if (foto != null && !foto.isEmpty()) {
                planta.setFotoPlanta(foto.getBytes());
            }

            Planta nuevaPlanta = plantaService.crearPlanta(planta);
            return new ResponseEntity<>(nuevaPlanta, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity("Error al procesar la solicitud de la planta: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity("Error interno del servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Planta> getPlantaById(@PathVariable Long id) {
        Optional<Planta> planta = plantaService.getPlantaById(id);
        return planta.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Planta>> getPlantasByUsuario(@PathVariable Long userId) {
        Optional<Usuario> optionalUsuario = usuarioService.getUsuarioById(userId);
        if (optionalUsuario.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Planta> plantas = plantaService.getPlantasByUsuario(optionalUsuario.get());
        return new ResponseEntity<>(plantas, HttpStatus.OK);
    }
    
    // endpoint para actualizar una planta (incluyendo la foto)
    // PUT /api/plantas/{id}
    // Consume multipart/form-data
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Planta> updatePlanta(
            @PathVariable Long id,
            @RequestPart("planta") String plantaJson,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        try {
            Planta plantaDetails = objectMapper.readValue(plantaJson, Planta.class);
            // No se debe permitir cambiar el usuario de la planta directamente desde aquí
            // plantaDetails.setUsuario(null); // Asegurarse de que el usuario no se actualice accidentalmente

            // Si se proporciona una foto, convertirla a byte[] y asignarla
            if (foto != null && !foto.isEmpty()) {
                plantaDetails.setFotoPlanta(foto.getBytes());
            } else {
                // Si no se proporciona una nueva foto, y quieres mantener la existente,
                // primero recupera la planta actual y copia su foto.
                // O si la foto viene como null y no se envía archivo, se asume que se quiere eliminar la foto.
                // Para simplificar, si no se envía archivo, el campo fotoPlanta en plantaDetails será null.
                // Si quieres mantener la foto existente si no se envía una nueva,
                // necesitarías un paso adicional para obtener la planta actual de la DB.
            }

            Planta updatedPlanta = plantaService.updatePlanta(id, plantaDetails);
            if (updatedPlanta != null) {
                return new ResponseEntity<>(updatedPlanta, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity("Error al procesar la solicitud de la planta: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity("Error interno del servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanta(@PathVariable Long id) {
        plantaService.deletePlanta(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
