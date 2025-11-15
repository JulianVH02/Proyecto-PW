package com.proyecto.sinergia.dto;

import com.proyecto.sinergia.model.Tarea;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TareaDTO {
    private Long id;
    private String title; // FullCalendar usa 'title'
    private LocalDateTime start; // FullCalendar usa 'start'
    private LocalDateTime end;   // FullCalendar usa 'end'
    private String backgroundColor; // Color del evento
    private String borderColor;
    private String description; // Campo extra nuestro

    public TareaDTO(Tarea tarea) {
        this.id = tarea.getId();
        this.title = tarea.getTitulo();
        this.start = tarea.getFechaInicio();
        this.end = tarea.getFechaFin();
        this.backgroundColor = tarea.getColor();
        this.borderColor = tarea.getColor();
        this.description = tarea.getDescripcion();
    }
}