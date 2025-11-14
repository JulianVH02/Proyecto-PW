package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import java.sql.Date;
import lombok.Data;

@Data
@Entity
@Table(name = "tarea")
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_inicio")
    private Date fechaInicio;

    @Column(name = "fecha_fin")
    private Date fechaFin;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean completada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario") // ON DELETE CASCADE
    private Usuario usuario;
}