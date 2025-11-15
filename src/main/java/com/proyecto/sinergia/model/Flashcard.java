package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "flashcard")
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String pregunta;

    @Column(nullable = false, length = 1000)
    private String respuesta;

    // Opcional: Para organizarlas por materia
    @Column(name = "materia_nombre")
    private String materia; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}