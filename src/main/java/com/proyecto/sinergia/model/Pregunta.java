package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString; // IMPORTANTE
import java.util.List;
import com.fasterxml.jackson.annotation.JsonBackReference;    // <--- NUEVO
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Entity
@Table(name = "pregunta")
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String textoPregunta;

    @ManyToOne
    @JoinColumn(name = "id_quiz")
    @ToString.Exclude
    @JsonBackReference // <--- ESTO DICE: "No vuelvas a mostrar al Quiz, corta el bucle aquí"
    private Quiz quiz;

    @OneToMany(mappedBy = "pregunta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonManagedReference // <--- ESTO DICE: "Muestra mis hijos (las opciones)"
    private List<Opcion> opciones;
}