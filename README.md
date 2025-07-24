# Megan: Tu Asistente Personal para el Cuidado de Plantas
Megan es una aplicación backend (API RESTful) diseñada para ayudar a los amantes de las plantas a gestionar y cuidar sus colecciones. Permite a los usuarios registrar sus plantas, llevar un seguimiento de los riegos, y recibir notificaciones personalizadas sobre el cuidado de sus plantas.

Este proyecto backend está construido con Spring Boot y utiliza MySQL como base de datos.

## Características Principales

- Gestión de Usuarios: Registro y autenticación segura de usuarios.

- Gestión de Plantas: CRUD (Crear, Leer, Actualizar, Eliminar) de plantas, incluyendo la capacidad de almacenar fotos.

- Registro de Riegos: Seguimiento detallado de cada evento de riego por planta.

- Sistema de Notificaciones: Generación y gestión de notificaciones (ej., recordatorios de riego, confirmaciones).

## Tecnologías Utilizadas

### Backend

- Java 17: Lenguaje de programación.

- Spring Boot 3.4.7: Framework para el desarrollo rápido de aplicaciones Java.

- Spring Data JPA: Abstracción para la persistencia de datos con Hibernate.

- Spring Security: Para el hashing de contraseñas y seguridad básica.

- Apache Tomcat (Embebido): Servidor web para servir la API REST.

### Base de Datos

- MySQL 8.0+: Base de datos relacional.

- HikariCP: Pool de conexiones JDBC de alto rendimiento.

### Herramientas de Construcción y Gestión

- Maven: Herramienta de gestión de proyectos y construcción.

## Configuración y Ejecución del Backend

### Requisitos Previos

- Java Development Kit (JDK) 17 o superior.

- MySQL Server 8.0 o superior.

- Un cliente MySQL (ej. MySQL Workbench).

- Apache NetBeans IDE.

### 1. Configuración de la Base de Datos MySQL

### 1.1 Crear la Base de Datos:

Abre tu cliente MySQL y ejecuta el siguiente comando para crear la base de datos:

CREATE DATABASE IF NOT EXISTS `megan_db`;

### 1.2 Crear Tablas y Estructura:

Una vez creada la base de datos, ejecutar el script SQL completo que define las tablas usuarios, plantas, riegos y notificaciones. Asegurarse de que los tipos de datos de los IDs sean BIGINT para compatibilidad con Java Long.

(Se puede copiar el contenido del script SQL completo aquí)

-- Ejemplo de la estructura de tabla (simplificado)
USE `megan_db`;

CREATE TABLE IF NOT EXISTS `usuarios` (
    `id_usuario` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `nombre` VARCHAR(100) NOT NULL,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

### 2. Configuración del Proyecto Spring Boot

### 2.1 Clonar el Repositorio:

git clone <URL_DE_TU_REPOSITORIO_GITHUB>
cd megan

### 2.2 Configurar application.properties:

Crea o edita el archivo src/main/resources/application.properties y añade la configuración de tu base de datos:



