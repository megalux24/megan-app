// src/main/java/com/megan/config/SecurityConfig.java
package com.megan.config;

import com.megan.model.Usuario;
import com.megan.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder; // Nueva importación
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioService usuarioService) {
        return email -> {
            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
            }
            return User.withUsername(usuario.getEmail())
                       .password(usuario.getPassword())
                       .roles("USER")
                       .build();
        };
    }

    // Configura el AuthenticationManager para usar nuestro UserDetailsService y PasswordEncoder
    // @Autowired // @Autowired no es necesario en el constructor si solo hay uno
    public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/usuarios/registrar", "/api/usuarios/login").permitAll()
                .requestMatchers("/", "/index.html", "/style.css", "/script.js", "/favicon.ico", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(); // Habilita la autenticación básica HTTP

        return http.build();
    }
}
