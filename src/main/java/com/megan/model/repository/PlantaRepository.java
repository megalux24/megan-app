/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.megan.model.repository;

import com.megan.model.Planta;
import com.megan.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 *
 * @author lucasayala
 */
@Repository
public interface PlantaRepository extends JpaRepository<Planta, Long> {
    
    List<Planta> findByUsuario(Usuario usuario);
    List<Planta> findByNombreComunContainingIgnoreCase(String nombreComun);
    
}
