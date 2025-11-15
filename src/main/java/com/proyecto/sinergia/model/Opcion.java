package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString; // IMPORTANTE
import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@Entity
@Table(name = "opcion")
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String textoOpcion;

    @Column(nullable = false)
    private boolean esCorrecta;

    @ManyToOne
    @JoinColumn(name = "id_pregunta")
    @ToString.Exclude
    @JsonBackReference // <--- ESTO DICE: "No vuelvas a mostrar la Pregunta"
    private Pregunta pregunta;
}