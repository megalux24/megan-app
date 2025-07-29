package com.megan.controller;

import com.megan.model.Usuario;
import com.megan.service.UsuarioService;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, AuthenticationManager authenticationManager) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
    }

    // DTO (Data Transfer Object) para datos de entrada/salida.
    public static class UserInfo {
        private Long idUsuario;
        private String nombre;
        private String email;

        public UserInfo(Long idUsuario, String nombre, String email) {
            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.email = email;
        }
        // Getters y Setters
        public Long getIdUsuario() { return idUsuario; }
        public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // DTO para el login
    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Optional<Usuario> usuarioOpt = usuarioService.findByEmail(loginRequest.getEmail());
            if (usuarioOpt.isPresent()) {
                UserInfo userInfo = new UserInfo(usuarioOpt.get().getIdUsuario(), usuarioOpt.get().getNombre(), usuarioOpt.get().getEmail());
                return ResponseEntity.ok(userInfo);
            } else {
                return new ResponseEntity<>("Error interno: Usuario autenticado no encontrado.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Credenciales inválidas.", HttpStatus.UNAUTHORIZED);
        }
    }

        @PostMapping("/registrar")
    public ResponseEntity<Void> registrarUsuario(@RequestBody Usuario usuario) {
        System.out.println("---[CONTROLLER] Entrando a registrarUsuario...");
        try {
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
            
            System.out.println("---[CONTROLLER] Usuario registrado en el servicio. ID: " + nuevoUsuario.getIdUsuario());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{id}")
                    .buildAndExpand(nuevoUsuario.getIdUsuario()).toUri();

            System.out.println("---[CONTROLLER] Ubicación creada. Preparando para devolver ResponseEntity...");
            
            ResponseEntity<Void> response = ResponseEntity.created(location).build();

            System.out.println("---[CONTROLLER] ResponseEntity creado. Saliendo del método.");
            return response;

        } catch (Exception e) {
            System.out.println("---[CONTROLLER] ERROR en registrarUsuario: " + e.getMessage());
            // Si el error es el StackOverflow, este log podría no aparecer.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
        
    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioById(id);
        if (usuarioOpt.isPresent()) {
            UserInfo userInfo = new UserInfo(usuarioOpt.get().getIdUsuario(), usuarioOpt.get().getNombre(), usuarioOpt.get().getEmail());
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<UserInfo>> getUsuarios(@RequestParam(required = false) String email) {
        if (email != null && !email.isEmpty()) {
            // Devuelve una lista (aunque sea de un solo elemento) para mantener el tipo de respuesta consistente
            List<UserInfo> userInfoList = usuarioService.findByEmail(email)
                .stream()
                .map(u -> new UserInfo(u.getIdUsuario(), u.getNombre(), u.getEmail()))
                .collect(Collectors.toList());
            return new ResponseEntity<>(userInfoList, HttpStatus.OK);
        } else {
            List<UserInfo> userInfos = usuarioService.getAllUsuarios().stream()
                .map(u -> new UserInfo(u.getIdUsuario(), u.getNombre(), u.getEmail()))
                .collect(Collectors.toList());
            return new ResponseEntity<>(userInfos, HttpStatus.OK);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        try {
            Usuario updatedUsuario = usuarioService.updateUsuario(id, usuarioDetails);
            UserInfo userInfo = new UserInfo(updatedUsuario.getIdUsuario(), updatedUsuario.getNombre(), updatedUsuario.getEmail());
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        try {
            usuarioService.deleteUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}