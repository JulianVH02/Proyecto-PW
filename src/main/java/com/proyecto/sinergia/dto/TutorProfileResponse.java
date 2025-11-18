package com.proyecto.sinergia.dto;

import com.proyecto.sinergia.model.Tutor;
import com.proyecto.sinergia.model.TutorRating;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TutorProfileResponse {

    private Long idTutor;
    private String nombreCompleto;
    private String materia;
    private String descripcion;
    private String contacto;
    private BigDecimal precioPorClase;
    private Double ratingPromedio;

    private List<TutorCommentDto> comentarios;
    private Integer totalComentarios;

    public TutorProfileResponse() {}

    public TutorProfileResponse(Tutor tutor) {
        this.idTutor = tutor.getId();
        this.nombreCompleto = tutor.getUsuario().getNombre() + " " + tutor.getUsuario().getApellido();
        this.materia = Optional.ofNullable(tutor.getMateria()).map(m -> m.getNombre()).orElse("General");
        this.descripcion = tutor.getDescripcion();
        this.contacto = tutor.getContacto();
        this.precioPorClase = tutor.getPrecioPorClase();

        List<TutorRating> ratings = tutor.getRatings();
        if (ratings != null && !ratings.isEmpty()) {
            double total = ratings.stream().mapToInt(TutorRating::getPuntuacion).sum();
            this.ratingPromedio = Math.round((total / ratings.size()) * 10.0) / 10.0;
        } else {
            this.ratingPromedio = 0.0;
        }

        if (ratings != null && !ratings.isEmpty()) {
            this.comentarios = ratings.stream()
                    .filter(r -> r.getComentario() != null && !r.getComentario().trim().isEmpty())
                    .map(r -> new TutorCommentDto(
                            r.getEstudiante().getNombre() + " " + r.getEstudiante().getApellido(),
                            r.getPuntuacion(),
                            r.getComentario(),
                            r.getFechaCreacion(),
                            r.getEstudiante().getFotoPerfil()
                    ))
                    .collect(Collectors.toList());
        } else {
            this.comentarios = new ArrayList<>();
        }

        this.totalComentarios = this.comentarios != null ? this.comentarios.size() : 0;
    }

    public Long getIdTutor() {
        return idTutor;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getMateria() {
        return materia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getContacto() {
        return contacto;
    }

    public BigDecimal getPrecioPorClase() {
        return precioPorClase;
    }

    public Double getRatingPromedio() {
        return ratingPromedio;
    }

    public List<TutorCommentDto> getComentarios() {
        return comentarios;
    }

    public Integer getTotalComentarios() {
        return totalComentarios;
    }
}
