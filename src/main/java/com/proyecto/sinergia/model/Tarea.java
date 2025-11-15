package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tarea")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    // Fecha de inicio del evento/tarea
    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    // Fecha de fin (opcional, si es solo un recordatorio puntual)
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private String color; // Para que el usuario elija el color en el calendario (ej. #ff0000)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}