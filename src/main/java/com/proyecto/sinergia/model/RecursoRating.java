package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "recurso_rating")
public class RecursoRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Puntuación de 1 a 5
    @Column(nullable = false)
    private Integer puntuacion;

    // Quién califica (Usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Qué recurso es calificado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recurso", nullable = false)
    private Recurso recurso;
}