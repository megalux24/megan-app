/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.megan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author lucasayala
 */
@Configuration // indicamos que la clase tiene definiciones de beans de Spring
public class SecurityConfig {
    
    @Bean // Marca este método para que Spring cree un bean de PasswordEncoder
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder es un algoritmo de hashing de contraseñas robusto y recomendado
        return new BCryptPasswordEncoder();
    }
    
}
    

