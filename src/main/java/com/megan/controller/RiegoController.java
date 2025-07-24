/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.controller;

import com.megan.model.Planta; //para asociar el riego a la planta
import com.megan.model.Riego;
import com.megan.service.PlantaService; //para obtener la planta
import com.megan.service.RiegoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author lucasayala
 */
@RestController
@RequestMapping("/api/riegos")
public class RiegoController {
    private final RiegoService riegoService;
    private final PlantaService plantaService; // Para obtener la planta a la que se asocia el riego

    @Autowired
    public RiegoController(RiegoService riegoService, PlantaService plantaService) {
        this.riegoService = riegoService;
        this.plantaService = plantaService;
    }
    
    // registrar un riego para una planta 
    @PostMapping
    public ResponseEntity<Riego> registrarRiego(@RequestBody Riego riego) {
        // Asegurarse de que el riego tenga una planta asociada válida
        if (riego.getPlanta() == null || riego.getPlanta().getIdPlanta() == null) {
            return new ResponseEntity("El ID de la planta es requerido para registrar un riego.", HttpStatus.BAD_REQUEST);
        }

        Optional<Planta> optionalPlanta = plantaService.getPlantaById(riego.getPlanta().getIdPlanta());
        if (optionalPlanta.isEmpty()) {
            return new ResponseEntity("Planta no encontrada.", HttpStatus.NOT_FOUND);
        }
        riego.setPlanta(optionalPlanta.get()); // Asignar la planta completa al objeto riego

        Riego nuevoRiego = riegoService.registrarRiego(riego);
        return new ResponseEntity<>(nuevoRiego, HttpStatus.CREATED);
    }
    
    // GET /api/riegos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Riego> getRiegoById(@PathVariable Long id) {
        Optional<Riego> riego = riegoService.getRiegoById(id);
        return riego.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/planta/{plantaId}")
    public ResponseEntity<List<Riego>> getRiegosByPlanta(@PathVariable Long plantaId) {
        Optional<Planta> optionalPlanta = plantaService.getPlantaById(plantaId);
        if (optionalPlanta.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Riego> riegos = riegoService.getRiegosByPlanta(optionalPlanta.get());
        return new ResponseEntity<>(riegos, HttpStatus.OK);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRiego(@PathVariable Long id) {
        riegoService.deleteRiego(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    
}
