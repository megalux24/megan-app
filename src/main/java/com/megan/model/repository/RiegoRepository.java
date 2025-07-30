/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.megan.model.repository;

import com.megan.model.Riego;
import com.megan.model.Planta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 *
 * @author lucasayala
 */
@Repository
public interface RiegoRepository extends JpaRepository<Riego, Long> {
    
    List<Riego> findByPlanta(Planta planta);
    Riego findTopByPlantaOrderByFechaHoraRiegoDesc(Planta planta);
    
}
