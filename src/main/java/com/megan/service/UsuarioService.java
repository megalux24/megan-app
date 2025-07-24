/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.service;

import com.megan.model.Usuario;
import com.megan.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author lucasayala
 */
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // Declarar PasswordEncoder
    
    // Inyección de dependencias del repositorio y del PasswordEncoder a través del constructor
    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder; // Asignar PasswordEncoder
    }
    
    public Usuario registrarUsuario(Usuario usuario) {
        // hasheamos la contraseña antes de guardar!
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setFechaRegistro(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }
    
    // Método para autenticar un usuario de forma segura
    public Optional<Usuario> autenticarUsuario(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        // Comparamos la contraseña proporcionada con el hash almacenado
        if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
            return Optional.of(usuario);
        }
        return Optional.empty();
    }
         
    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }
    
    public Usuario updateUsuario(Long id, Usuario usuarioDetails) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuarioExistente = optionalUsuario.get();
            usuarioExistente.setNombre(usuarioDetails.getNombre());
            usuarioExistente.setEmail(usuarioDetails.getEmail());
            //Si se necesita cambiar la contraseña,debería ser a través de un método específico que la hashee de nuevo.
            return usuarioRepository.save(usuarioExistente);
        }
        return null; // O lanzar una excepción
    }
    
    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
    
}
