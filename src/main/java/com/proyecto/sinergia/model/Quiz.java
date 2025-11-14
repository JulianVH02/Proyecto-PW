package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String tema;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario") // ON DELETE CASCADE
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia") // ON DELETE SET NULL
    private Materia materia;

    // Relación: Un Quiz tiene muchas Preguntas
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pregunta> preguntas;
}