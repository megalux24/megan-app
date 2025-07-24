/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.controller;

import com.megan.model.Usuario;
import com.megan.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;

/**
 *
 * @author lucasayala
 */
@RestController
@RequestMapping("api/usuarios")//ruta base para todos los endpoints de este controller
public class UsuarioController {
    //prepara al UsuarioController para recibir y utilizar una instancia del UsuarioService, 
    //que es el componente que contiene la lógica de negocio para gestionar los usuarios
    private final UsuarioService usuarioService; //final porque solo se instancia una vez
    
    @Autowired // Inyección de dependencias del UsuarioService, le inyectamos penicilina para que maneje la infexion 
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }
    
    @PostMapping("/registrar") 
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED); // Devuelve 201 Created
        } catch (DataIntegrityViolationException e) {
            // Esto ocurre, por ejemplo, si el email ya existe (debido a la restricción UNIQUE en la DB)
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Devuelve 409 Conflict
        } catch (Exception e) {
            // Captura cualquier otra excepción inesperada
            // logger.error("Error inesperado al registrar usuario", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Devuelve 500 Internal Server Error
        }
    }
    
    // Endpoint para autenticar un usuario
    // Espera un JSON con "email" y "password"
    @PostMapping("/login")
    public ResponseEntity<String> loginUsuario(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return new ResponseEntity<>("Email y contraseña son requeridos", HttpStatus.BAD_REQUEST);
        }

        Optional<Usuario> usuarioAutenticado = usuarioService.autenticarUsuario(email, password);

        if (usuarioAutenticado.isPresent()) {
            // En una aplicación real, aquí se generaría un token JWT o se establecería una sesión
            return new ResponseEntity<>("Autenticación exitosa para el usuario: " + usuarioAutenticado.get().getEmail(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Credenciales inválidas", HttpStatus.UNAUTHORIZED); // Devuelve 401 Unauthorized
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Devuelve 404 Not Found si no existe
    }
    
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        Usuario updatedUsuario = usuarioService.updateUsuario(id, usuarioDetails);
        if (updatedUsuario != null) {
            return new ResponseEntity<>(updatedUsuario, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Devuelve 404 Not Found
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Devuelve 204 No Content
    }
    
    
    
    
}
