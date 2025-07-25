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

Abrir el cliente MySQL y ejecutar el siguiente comando para crear la base de datos:

CREATE DATABASE IF NOT EXISTS `megan_db`;

### 1.2 Crear Tablas y Estructura:

El script SQL completo que define las tablas usuarios, plantas, riegos y notificaciones se encuentra en src/main/resources/sql.

 Es importante asegurarse de que los tipos de datos de los IDs sean BIGINT para compatibilidad con Java Long.


### 2. Configuración del Proyecto Spring Boot

### 2.1 Clonar el Repositorio:

git clone https://github.com/megalux24/megan-app.git
cd megan

### 2.2 Configurar application.properties:

Crear o editar el archivo src/main/resources/application.properties y añade la configuración de tu base de datos:

spring.datasource.url=jdbc:mysql://localhost:3306/megan_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=usuario_mysql
spring.datasource.password=contrasenya_mysql
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update # update define el comportamiento de hibernate con la DB, es importante tener en cuenta que en otros entornos (producción por ej.) debe cambiar
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect # Opcional en MySQL 8+

Importante: Reemplazar usuario_mysql y contrasenya_mysql con tus credenciales reales de MySQL.

### 3. Ejecutar la Aplicación

Desde Apache NetBeans:

1. Abre el proyecto megan.

2. Navega a src/main/java/com/megan/MeganApplication.java.

3. Haz clic derecho sobre MeganApplication.java y selecciona "Run File".

La aplicación se iniciará y estará disponible en http://localhost:8080.

## Endpoints de la API REST 

La API está disponible en http://localhost:8080/api/. Aquí se listan los principales endpoints:

### Usuarios (/api/usuarios) 

### POST /api/usuarios/registrar 

+ Descripción: Registra un nuevo usuario. La contraseña se hashea antes de guardarse. 

+ Cuerpo de la solicitud (JSON) 

{
    "nombre": "Nombre del Usuario",
    "email": "email@example.com",
    "password": "contraseñaSegura123"
}

+ Respuestas: 201 Created (éxito), 409 Conflict (email ya existe), 500 Internal Server Error.

### POST /api/usuarios/login 

+ Descripción: Autentica un usuario.

+ Cuerpo de la solicitud (JSON) 

{
    "email": "email@example.com",
    "password": "contraseñaSegura123"
}

+ Respuestas: 200 OK (éxito), 400 Bad Request (datos faltantes), 401 Unauthorized (credenciales inválidas).

### GET /api/usuarios/{id} 

+ Descripción: Obtiene un usuario por su ID.

+ Respuestas 200 OK (usuario encontrado), 404 Not Found. 

### GET /api/usuarios 

+ Descripción: Obtiene una lista de todos los usuarios. 

+ Respuestas: 200 OK. 

### PUT /api/usuarios/{id}

+ Descripción: Actualiza la información de un usuario.

+ Cuerpo de la solicitud (JSON): (Ejemplo para update de nombre y email)

{
    "nombre": "Nuevo Nombre",
    "email": "nuevo_email@example.com"
}

+ Respuestas: 200 OK (actualizado), 404 Not Found. 

### DELETE /api/usuarios/{id} 

+ Descripción: Elimina un usuario por su ID. 

+ Respuestas: 204 No Content. 


### Plantas (/api/plantas)


### POST /api/plantas 

+ Descripción: Crea una nueva plantas, opcionalmente con una foto.

+ Cuerpo de la solicitud (multipart/form-data):
    - planta(tipo Text): JSON String de los datos de la planta. Debe incluir usuario: {"idUsuario":
      ID_DEL_USUARIO}
        {
            "usuario": {"idUsuario": 1},
            "nombreComun": "Monstera Deliciosa",
            "nombreCientifico": "Monstera deliciosa",
            "ubicacion": "Sala de estar",
            "fechaAdquisicion": "2023-01-15",
            "notas": "Necesita mucha luz indirecta.",
            "frecuenciaRiegoDias": 7
        }

+ Respuestas: 201 Created, 400 Bad Request, 404 Not Found (usuario inexistente), 500 Internal
Server Error. 

### GET /api/plantas/{id} 

+ Descripción: Obtiene plantas por su ID.

+ Respuestas 200 OK, 404 Not Found. 

### GET /api/plantas/usuario/{userID}

+ Descripción: Obtiene todas las plantas de un usuario específico. 

+ Respuestas: 200 OK, 404 Not Found (usuario inexistente).

### PUT /api/plantas/{id}

+ Descripción: Actualiza una planta existente, opcionalmente con una nueva foto. 

+ Cuerpo de la solicitud (multipart/form-data): Similar al POST, pero para actualizar. 

+ Respuestas: 200 OK, 400 Bad Request, 404 Not Found. 

### DELETE /api/plantas/{id}

+ Descripción: Elimina una planta por su ID. 

+ Respuestas: 204 No Content. 


### Riegos (/api/riegos) 


### POST /api/riegos 

+ Descripción: Regitra un nuevo evento de riego para una planta. Genera una notificación de confirmación.

+ Cuerpo de la Solicitud (JSON): 

{
    "planta": {"idPlanta": 1},
    "cantidadAguaMl": 500.00,
    "observaciones": "Riego abundante, la tierra estaba muy seca."
}

+ Respuestas: 201 Created, 400 Bad Request, 404 Not Found (planta inexistente).

### GET /api/riegos/{id}

+ Descripción: Obtiene un registro de riego por su ID.

+ Respuestas: 200 OK, 404 Not Found.

### GET /api/riegos/planta/{plantaId}

+ Descripción: Obtiene todos los registros de riego para una planta específica.

+ Respuestas: 200 OK, 404 Not Found (si la planta no existe).

### DELETE /api/riegos/{id}

+ Descripción: Elimina un registro de riego por su ID.

+ Respuestas: 204 No Content.


### Notificaciones (/api/notificaciones)


### GET /api/notificaciones/usuario/{userId}

+ Descripción: Obtiene todas las notificaciones (leídas y no leídas) para un usuario.

+ Respuestas: 200 OK, 404 Not Found (si el usuario no existe).

### GET /api/notificaciones/usuario/{userId}/no-leidas

+ Descripción: Obtiene solo las notificaciones no leídas para un usuario.

+ Respuestas: 200 OK, 404 Not Found (si el usuario no existe).

### PUT /api/notificaciones/{id}/marcar-leida

+ Descripción: Marca una notificación específica como leída (actualizar su estado).

+ Respuestas: 200 OK (actualizada), 404 Not Found.

### DELETE /api/notificaciones/{id}

+ Descripción: Elimina una notificación por su ID.

+ Respuestas: 204 No Content.



