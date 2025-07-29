// src/main/java/com/megan/controller/PlantaController.java
package com.megan.controller;

import com.fasterxml.jackson.databind.ObjectMapper; // Necesario para convertir JSON String a objeto
import com.megan.model.Planta;
import com.megan.model.Usuario; // asociar la planta al usuario
import com.megan.service.PlantaService;
import com.megan.service.UsuarioService; // para obtener el usuario
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // Para especificar el tipo de contenido
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Para manejar la carga de archivos

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/plantas")
public class PlantaController {

    private final PlantaService plantaService;
    private final UsuarioService usuarioService; // Para obtener el usuario asociado a la planta
    private final ObjectMapper objectMapper; // Para convertir JSON a objeto

    @Autowired
    public PlantaController(PlantaService plantaService, UsuarioService usuarioService, ObjectMapper objectMapper) {
        this.plantaService = plantaService;
        this.usuarioService = usuarioService;
        this.objectMapper = objectMapper;
    }

    // Endpoint para crear una nueva planta con o sin foto
    // POST /api/plantas
    // Consume multipart/form-data para permitir JSON y archivo
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearPlanta( 
            @RequestPart("planta") String plantaJson, // Datos de la planta como JSON String
            @RequestPart(value = "foto", required = false) MultipartFile foto, // Archivo de la foto (opcional)
            @RequestParam("idUsuario") Long idUsuario) { // ID de usuario como RequestParam
        try {
            Planta planta = objectMapper.readValue(plantaJson, Planta.class); // Convertir JSON String a objeto Planta

            // Obtener el usuario por el idUsuario recibido como parámetro
            Optional<Usuario> optionalUsuario = usuarioService.getUsuarioById(idUsuario);
            if (optionalUsuario.isEmpty()) {
                // Mensaje más específico para el frontend
                return new ResponseEntity<>("Error: Usuario no encontrado con ID: " + idUsuario, HttpStatus.NOT_FOUND);
            }
            planta.setUsuario(optionalUsuario.get()); // Asignar el objeto Usuario completo a la planta

            // Si se proporciona una foto, convertirla a byte[] y asignarla
            if (foto != null && !foto.isEmpty()) {
                planta.setFotoPlanta(foto.getBytes());
            }

            Planta nuevaPlanta = plantaService.crearPlanta(planta);
            return new ResponseEntity<>(nuevaPlanta, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Error al procesar la solicitud de la planta (JSON o foto): " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Captura cualquier otra excepción y devuelve un mensaje útil
            return new ResponseEntity<>("Error interno del servidor al crear planta: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // GET /api/plantas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Planta> getPlantaById(@PathVariable Long id) {
        Optional<Planta> planta = plantaService.getPlantaById(id);
        return planta.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                     .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint para obtener todas las plantas de un usuario específico
    // GET /api/plantas/usuario/{userId}
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<Planta>> getPlantasByUsuario(@PathVariable Long userId) {
        Optional<Usuario> optionalUsuario = usuarioService.getUsuarioById(userId);
        if (optionalUsuario.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Planta> plantas = plantaService.getPlantasByUsuario(optionalUsuario.get());
        return new ResponseEntity<>(plantas, HttpStatus.OK);
    }

    // Endpoint para actualizar una planta (incluyendo la foto)
    // PUT /api/plantas/{id}
    // Consume multipart/form-data
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePlanta(
            @PathVariable Long id,
            @RequestPart("planta") String plantaJson,
            @RequestPart(value = "foto", required = false) MultipartFile foto,
            @RequestParam("idUsuario") Long idUsuario) { // ID de usuario como RequestParam
        try {
            Planta plantaDetails = objectMapper.readValue(plantaJson, Planta.class);
            
            // Obtener el usuario por el idUsuario recibido como parámetro
            Optional<Usuario> optionalUsuario = usuarioService.getUsuarioById(idUsuario);
            if (optionalUsuario.isEmpty()) {
                return new ResponseEntity<>("Error: Usuario no encontrado con ID: " + idUsuario, HttpStatus.NOT_FOUND);
            }
            plantaDetails.setUsuario(optionalUsuario.get()); // Asignar el objeto Usuario completo a la planta

            // Si se proporciona una nueva foto, asignarla
            if (foto != null && !foto.isEmpty()) {
                plantaDetails.setFotoPlanta(foto.getBytes());
            } else {
                // Si NO se proporciona una nueva foto, obtener la planta existente
                // y copiar su foto actual para mantenerla.
                Optional<Planta> existingPlanta = plantaService.getPlantaById(id);
                if (existingPlanta.isPresent()) {
                    plantaDetails.setFotoPlanta(existingPlanta.get().getFotoPlanta());
                }
                // Si existingPlanta no está presente, plantaDetails.fotoPlanta seguirá siendo null,
                // lo cual es correcto si la planta no existía o no tenía foto.
            }

            Planta updatedPlanta = plantaService.updatePlanta(id, plantaDetails);
            if (updatedPlanta != null) {
                return new ResponseEntity<>(updatedPlanta, HttpStatus.OK);
            }
            return new ResponseEntity<>("Planta con ID: " + id + " no encontrada para actualizar.", HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>("Error al procesar la solicitud de la planta (JSON o foto): " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno del servidor al actualizar planta: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlanta(@PathVariable Long id) {
        try {
            plantaService.deletePlanta(id);
            return new ResponseEntity<>("Planta eliminada con éxito.", HttpStatus.NO_CONTENT); // 204 No Content
        } catch (IllegalArgumentException e) {
            // Captura la excepción lanzada por el servicio si la planta no se encuentra
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada
            return new ResponseEntity<>("Error interno del servidor al eliminar planta: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }
}
