package com.proyecto.sinergia.model;

import com.proyecto.sinergia.model.enums.RolUsuario; 
import com.proyecto.sinergia.model.enums.RolUsuarioConverter; // Importamos el conversor
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import lombok.Data;
import jakarta.persistence.CascadeType;


@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(nullable = false, length = 255)
    private String contraseña;

    // --- CAMBIO CLAVE: SOLO USAMOS EL CONVERSOR ---
    // El @Convert le dice a JPA que use tu lógica de mapeo (minúsculas <-> mayúsculas).
    // ¡Eliminamos @Enumerated(EnumType.STRING) y columnDefinition!
    @Column(name = "rol", nullable = false) 
    @Convert(converter = RolUsuarioConverter.class) 
    private RolUsuario rol;

    @Column(name = "fecha_registro", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fechaRegistro;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean activo;

    // --- Relaciones ---
    
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Tutor tutor;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tarea> tareas;

    @OneToMany(mappedBy = "usuario")
    private List<Recurso> recursos;

    @OneToMany(mappedBy = "usuario")
    private List<Flashcard> flashcards;

    @OneToMany(mappedBy = "usuario")
    private List<Quiz> quizzes;
}