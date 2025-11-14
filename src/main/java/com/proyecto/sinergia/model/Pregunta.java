package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pregunta")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pregunta;

    @Column(columnDefinition = "TEXT")
    private String opcionA;
    @Column(columnDefinition = "TEXT")
    private String opcionB;
    @Column(columnDefinition = "TEXT")
    private String opcionC;
    @Column(columnDefinition = "TEXT")
    private String opcionD;

    @Column(name = "respuesta_correcta", length = 1)
    private String respuestaCorrecta; // CHAR(1)

    // Relación: Muchas Preguntas pertenecen a un Quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quiz", nullable = false) // ON DELETE CASCADE
    private Quiz quiz;
}