package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString; // IMPORTANTE
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "quiz")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;
    private String materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @ToString.Exclude // <--- AGREGA ESTO: Evita bucles al imprimir
    @JsonIgnore //
    private Usuario usuario;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude // <--- AGREGA ESTO: Vital para que funcione EAGER
    @JsonManagedReference
    private List<Pregunta> preguntas;
}