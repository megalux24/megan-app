// src/main/java/com/megan/model/Usuario.java
package com.megan.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonManagedReference; // Importar JsonManagedReference para el manejo correcto


@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    // Relación One-to-Many con Plantas: Un usuario puede tener muchas plantas
    // @JsonManagedReference indica que esta es la parte "gestora" de la relación y se serializará normalmente.                        
    @JsonManagedReference("usuario-plantas") 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Planta> plantas;
    
    // Relación One-to-Many con Notificaciones: Un usuario puede tener muchas notificaciones
    @JsonManagedReference("usuario-notificaciones") 
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notificacion> notificaciones;

    public Usuario() {
    }

    public Usuario(Long idUsuario, String nombre, String email, String password, LocalDateTime fechaRegistro, List<Planta> plantas) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.fechaRegistro = fechaRegistro;
        this.plantas = plantas;
    }

    public Usuario(String nombre, String email, String password, LocalDateTime fechaRegistro) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<Planta> getPlantas() {
        return plantas;
    }

    public void setPlantas(List<Planta> plantas) {
        this.plantas = plantas;
    }
    
     public List<Notificacion> getNotificaciones() { 
         return notificaciones; 
     }
     
     public void setNotificaciones(List<Notificacion> notificaciones) { 
         this.notificaciones = notificaciones; 
     }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(this.idUsuario, usuario.idUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.idUsuario);
    }

    @Override
    public String toString() {
        return "Usuario{" +
               "idUsuario=" + idUsuario +
               ", nombre='" + nombre + '\'' +
               ", email='" + email + '\'' +
               ", fechaRegistro=" + fechaRegistro +
               '}';
    }
}
