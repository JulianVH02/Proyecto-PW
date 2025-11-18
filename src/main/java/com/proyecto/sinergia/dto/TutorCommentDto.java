package com.proyecto.sinergia.dto;

import java.time.LocalDateTime;

public class TutorCommentDto {
    private String estudianteNombre;
    private Integer puntuacion;
    private String comentario;
    private LocalDateTime fecha;
    private String fotoPerfilEstudiante;

    public TutorCommentDto() {}

    public TutorCommentDto(String estudianteNombre, Integer puntuacion, String comentario, LocalDateTime fecha, String fotoPerfilEstudiante) {
        this.estudianteNombre = estudianteNombre;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.fecha = fecha;
        this.fotoPerfilEstudiante = fotoPerfilEstudiante;
    }

    public String getEstudianteNombre() {
        return estudianteNombre;
    }

    public void setEstudianteNombre(String estudianteNombre) {
        this.estudianteNombre = estudianteNombre;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getFotoPerfilEstudiante() {
        return fotoPerfilEstudiante;
    }

    public void setFotoPerfilEstudiante(String fotoPerfilEstudiante) {
        this.fotoPerfilEstudiante = fotoPerfilEstudiante;
    }
}
