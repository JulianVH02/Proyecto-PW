package com.proyecto.sinergia.dto;

import lombok.Data;

// DTO para recibir la data del formulario
@Data
public class RecursoRequest {
    private String titulo;
    private String url;
    private String descripcion;
    private String tipoRecurso; // Recibimos el String, ej "Video"
    private Long materiaId;
}