package com.proyecto.sinergia.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String materia;
    private List<PreguntaDTO> preguntas;

    @Data
    public static class PreguntaDTO {
        private String textoPregunta;
        private List<OpcionDTO> opciones;
    }

    @Data
    public static class OpcionDTO {
        private String textoOpcion;
        private boolean esCorrecta;
    }
}