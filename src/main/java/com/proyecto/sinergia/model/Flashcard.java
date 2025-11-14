package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import lombok.Data;

@Data
@Entity
@Table(name = "flashcard")
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pregunta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String respuesta;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario") // ON DELETE CASCADE
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia") // ON DELETE SET NULL
    private Materia materia;
}