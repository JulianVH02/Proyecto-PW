package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tutor_rating")
public class TutorRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Puntuación de 1 a 5
    @Column(nullable = false)
    private Integer puntuacion;

    // Estudiante que califica (usuario con rol ESTUDIANTE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Usuario estudiante;

    // Tutor calificado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tutor", nullable = false)
    private Tutor tutor;
    
    // Campo opcional para comentarios
    @Column(columnDefinition = "TEXT")
    private String comentario;
}