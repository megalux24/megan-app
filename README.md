# Megan: Tu Asistente de Plantas 🌱

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql)
![JavaScript](https://img.shields.io/badge/JavaScript-ES6-F7DF1E?style=for-the-badge&logo=javascript)

## Descripción

En la vorágine del día a día, es fácil olvidar tareas importantes como el cuidado de nuestras plantas. Megan nace para resolver este problema, ofreciendo una solución digital y centralizada para la gestión del cuidado de plantas.

En mi caso en particular, al tener una colección numerosa de plantas, me tomaba mucho tiempo tomar notas y configurar recordatorios para evitar el sobreriego o que se sequen. Ahora, con Megan, garantizo el registro adecuado de los riegos, me ahorro tiempo y mis plantas lucen notablemente contentas.

El proyecto está dirigido a cualquier persona, desde **principiantes en jardinería** que necesitan ayuda y seguimiento, hasta **amantes de las plantas con colecciones grandes** o **personas ocupadas** que se benefician de un sistema de registro y notificaciones para mantener sus plantas saludables. El enfoque principal del proyecto fue desarrollar una arquitectura de backend robusta, modular y segura, sentando las bases para un producto escalable.

## ✨ Funcionalidades Principales

* **👤 Gestión de Usuarios:** Sistema completo de registro e inicio de sesión con Spring Security y contraseñas encriptadas (BCrypt).
* **🌿 CRUD de Plantas:** Los usuarios pueden añadir, ver, editar y eliminar sus plantas, incluyendo una foto para cada una (`multipart/form-data`).
* **💧 Registro de Riegos:** Funcionalidad para anotar cada evento de riego, actualizando automáticamente la fecha de último riego de la planta.
* **📊Indicador Visual de Riego:** Iconos dinámicos en cada tarjeta que reflejan el estado de riego actual de la planta (feliz, normal, sedienta), calculado en tiempo real por el backend.
* **🔔 Sistema de Notificaciones:** Notificaciones en tiempo real para confirmar acciones clave como el registro de un riego.
* **🔒 API Segura:** Todos los endpoints que manejan datos personales están protegidos y requieren autenticación. La comunicación se realiza de forma segura a través de **HTTPS/TLS**.

## 🛠️ Stack Tecnológico

* **Backend:**
    * Java 17+ y Spring Boot 3
    * Spring Security (Autenticación Basic)
    * JPA / Hibernate (ORM)
    * MySQL 8.0+
    * Maven (Gestión de Dependencias)
* **Frontend:**
    * HTML5 y CSS3 (con Tailwind CSS)
    * JavaScript (Vanilla JS, Fetch API)

## 🚀 Instalación y Despliegue Local

Sigue estos pasos para ejecutar el proyecto en tu máquina local.

### Requisitos Previos

-   Java Development Kit (JDK) 17 o superior.
-   MySQL Server 8.0 o superior.
-   Apache Maven 3.6+

### Pasos

1.  **Clonar el Repositorio**
    git clone https://github.com/megalux24/megan-app.git
    cd megan-app

2.  **Configurar la Base de Datos**
    -   Conéctate a tu instancia local de MySQL.
    -   Crea la base de datos: `CREATE DATABASE megan_db;`
    -   **Importante:** Ejecuta el script `src/main/resources/sql/schema.sql` que se encuentra en este repositorio. Este script creará todas las tablas con los tipos de datos correctos, especialmente `LONGBLOB` para las imágenes, evitando problemas con la generación automática de Hibernate.

3.  **Configurar la Aplicación**
    -   Abre el fichero `src/main/resources/application.properties`.
    -   Modifica los valores de `spring.datasource.username` y `spring.datasource.password` para que coincidan con tus credenciales de MySQL.
    -   Asegúrate de que spring.jpa.hibernate.ddl-auto esté en validate. Esto garantizará que la estructura creada por el script SQL no sea alterada accidentalmente por la aplicación, que fue uno de los problemas abordados durante el desarrollo.

4.  **Generar el Certificado SSL/TLS (para HTTPS)**
    -   Desde la carpeta raíz del proyecto en tu terminal, ejecuta el siguiente comando:
    ```bash
    keytool -genkeypair -alias megan -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore src/main/resources/keystore.p12 -validity 365 -dname "CN=localhost"
    ```
    -   Introduce una contraseña cuando se te pida (ej: `password`) y asegúrate de que coincida con la propiedad `server.ssl.key-store-password` en `application.properties`.

5.  **Construir y Ejecutar**
    -   **Opción A (Usando Maven en la terminal):**
        ```bash
        mvn spring-boot:run
        ```
    -   **Opción B (Desde tu IDE):**
        -   Abre el proyecto como un proyecto de Maven.
        -   Clean & Build.
        -   Ejecutar.

6.  **Acceder a la Aplicación**
    -   Abre tu navegador y ve a: **`https://localhost:8080`**
    -   Acepta la advertencia de seguridad del navegador (el certificado es autofirmado).

El proyecto actual sienta una base sólida. Las siguientes mejoras se proponen para futuras iteraciones:

* **Implementación de Recordatorios Proactivos:** Activar y mejorar la lógica de negocio para generar notificaciones de riego pendientes, utilizando tareas programadas en el backend (`@Scheduled`) para un sistema de alertas completamente autónomo.
* **Desarrollo de Pruebas Automatizadas:** Desarrollar un conjunto de pruebas unitarias (JUnit, Mockito) y de integración (Spring Boot Test) para automatizar la validación del backend, facilitando el mantenimiento y la escalabilidad.
* **Evolución del Sistema de Autenticación:** Migrar del sistema de autenticación básica actual a un esquema basado en tokens (ej. JWT - JSON Web Tokens), que es el estándar moderno para APIs REST y SPAs.
* **Integración con APIs Externas:** Conectar la aplicación a una API meteorológica para ajustar las sugerencias de riego de plantas de exterior según el clima.
* **Notificaciones PUSH:** Implementar notificaciones push a través de Service Workers en el navegador para que los recordatorios lleguen al usuario incluso si no tiene la aplicación abierta.


<br>

<details>
<summary>📚 Documentación de Endpoints de la API</summary>

### Usuarios (`/api/usuarios`)
-   `POST /registrar`: Registra un nuevo usuario.
-   `POST /login`: Autentica un usuario.

### Plantas (`/api/plantas`)
-   `POST /`: Crea una nueva planta.
    -   **Tipo:** `multipart/form-data`
    -   **Partes:**
        1.  `planta` (texto): Un string JSON con los datos de la planta.
        2.  `foto` (fichero): El archivo de imagen (opcional).
        3.  `idUsuario` (parámetro): El ID del usuario propietario.
-   `GET /{id}`: Obtiene los detalles de una planta (incluye observaciones del último riego).
-   `GET /usuario/{userId}`: Obtiene todas las plantas de un usuario.
-   `PUT /{id}`: Actualiza una planta existente.
-   `DELETE /{id}`: Elimina una planta.

### Riegos (`/api/riegos`)

-   `POST /`: Registra un **nuevo evento de riego** para una planta.
    -   **Cuerpo (JSON):**
        ```json
        {
          "plantaId": 1,
          "cantidadAguaMl": 250.5,
          "observaciones": "La tierra estaba bastante seca."
        }
        ```
-   `GET /planta/{plantaId}`: Obtiene el **historial completo de riegos** para una planta específica.

### Notificaciones (`/api/notificaciones`)
-   `GET /usuario/{userId}`: Obtiene todas las notificaciones de un usuario.
-   `PUT /{id}/marcar-leida`: Marca una notificación como leída.

</details>