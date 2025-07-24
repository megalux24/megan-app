/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.megan.model.repository;

import com.megan.model.Notificacion;
import com.megan.model.Usuario;
import com.megan.model.Planta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 *
 * @author lucasayala
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    List<Notificacion> findByUsuario(Usuario usuario);
    
    List<Notificacion> findByUsuarioAndLeidaFalse(Usuario usuario);
    
    List<Notificacion> findByPlanta(Planta planta);
            
}
