package com.megan.service;

import com.megan.model.Usuario;
import com.megan.model.repository.UsuarioRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; 

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método para registrar un nuevo usuario
    public Usuario registrarUsuario(Usuario usuario) {
        System.out.println("---[SERVICE] Entrando a registrarUsuario en el servicio...");
        
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            System.out.println("---[SERVICE] Email ya existe. Lanzando excepción.");
            throw new RuntimeException("El email ya está en uso: " + usuario.getEmail());
        }
        
        System.out.println("---[SERVICE] Email no existe. Encriptando contraseña...");
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setFechaRegistro(LocalDateTime.now());
        
        System.out.println("---[SERVICE] Contraseña encriptada. Llamando a repository.save()...");
        Usuario savedUsuario = usuarioRepository.save(usuario);
        System.out.println("---[SERVICE] repository.save() completado. Usuario guardado con ID: " + savedUsuario.getIdUsuario());

        System.out.println("---[SERVICE] Saliendo de registrarUsuario en el servicio.");
        return savedUsuario;
    }
    
    // Ahora devuelve Optional<Usuario>
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email); // Ahora devuelve directamente el Optional del repositorio
    }


    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }


    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }


    public Usuario updateUsuario(Long id, Usuario usuarioDetails) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        usuarioExistente.setNombre(usuarioDetails.getNombre());
        usuarioExistente.setEmail(usuarioDetails.getEmail());
        // No actualizamos la contraseña acá para evitar problemas de seguridad.
        // El cambio de contraseña debería ser un proceso separado.
        return usuarioRepository.save(usuarioExistente);
    }

 
    public void deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
