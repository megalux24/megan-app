CREATE DATABASE IF NOT EXISTS `megan_db`;

USE `megan_db`;

CREATE TABLE IF NOT EXISTS `usuarios` (
    `id_usuario` BIGINT AUTO_INCREMENT PRIMARY KEY, 
    `nombre` VARCHAR(100) NOT NULL,               
    `email` VARCHAR(100) NOT NULL UNIQUE,         
    `password` VARCHAR(255) NOT NULL,             
    `fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
);


CREATE TABLE IF NOT EXISTS `plantas` (
    `id_planta` BIGINT AUTO_INCREMENT PRIMARY KEY,     
    `id_usuario` BIGINT NOT NULL,                      
    `nombre_comun` VARCHAR(100) NOT NULL,           
    `nombre_cientifico` VARCHAR(100),              
    `ubicacion` VARCHAR(100),                       
    `fecha_adquisicion` DATE,                       
    `notas` TEXT,                                  
    `frecuencia_riego_dias` INT,                   
    `foto_planta` LONGBLOB,                         
    `ultima_fecha_riego` TIMESTAMP,                 

    FOREIGN KEY (`id_usuario`) REFERENCES `usuarios`(`id_usuario`) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS `riegos` (
    `id_riego` BIGINT AUTO_INCREMENT PRIMARY KEY,      
    `id_planta` BIGINT NOT NULL,                       
    `fecha_hora_riego` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    `cantidad_agua_ml` DECIMAL(7,2),               
    `observaciones` TEXT,                          

    FOREIGN KEY (`id_planta`) REFERENCES `plantas`(`id_planta`) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS `notificaciones` (
    `id_notificacion` BIGINT AUTO_INCREMENT PRIMARY KEY, 
    `id_usuario` BIGINT NOT NULL,                      
    `id_planta` BIGINT,                                
    `texto_notificacion` VARCHAR(500) NOT NULL,     
    `fecha_notificacion` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    `leida` BOOLEAN DEFAULT FALSE,                  
    -- Definición de claves foráneas
    FOREIGN KEY (`id_usuario`) REFERENCES `usuarios`(`id_usuario`) ON DELETE CASCADE,
    FOREIGN KEY (`id_planta`) REFERENCES `plantas`(`id_planta`) ON DELETE CASCADE
);

CREATE INDEX idx_plantas_usuario ON plantas(id_usuario);
CREATE INDEX idx_riegos_planta ON riegos(id_planta);
CREATE INDEX idx_notificaciones_usuario ON notificaciones(id_usuario);
CREATE INDEX idx_notificaciones_planta ON notificaciones(id_planta);