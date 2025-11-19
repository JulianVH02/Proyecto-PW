package com.proyecto.sinergia.dto;

import com.proyecto.sinergia.model.Tutor;
import com.proyecto.sinergia.model.TutorRating; // IMPORTAR
import lombok.Data;
import java.math.BigDecimal;
import java.util.Optional;

@Data
public class TutorResponseDto{
    private Long idTutor; // ID de la entidad Tutor
    private String nombreCompleto;
    private String materia;
    private String descripcion;
    private String contacto;
    private BigDecimal precioPorClase;
    private String fotoPerfil;
    private Integer totalRatings;
    
    // --- NUEVO CAMPO DE RATING ---
    private Double ratingPromedio; 
    // -----------------------------

    // Constructor para mapear fácil
    public TutorResponseDto(Tutor tutor) {
        this.idTutor = tutor.getId();
        this.nombreCompleto = tutor.getUsuario().getNombre() + " " + tutor.getUsuario().getApellido();
        this.materia = Optional.ofNullable(tutor.getMateria()).map(m -> m.getNombre()).orElse("General");
        this.descripcion = tutor.getDescripcion();
        this.contacto = tutor.getContacto();
        this.precioPorClase = tutor.getPrecioPorClase();
        this.fotoPerfil = Optional.ofNullable(tutor.getUsuario())
                .map(u -> u.getFotoPerfil())
                .orElse(null);
        this.totalRatings = tutor.getRatings() != null ? tutor.getRatings().size() : 0;

        // CALCULAMOS EL RATING PROMEDIO AQUÍ
        if (tutor.getRatings() != null && !tutor.getRatings().isEmpty()) {
            double totalPuntuacion = tutor.getRatings().stream()
                .mapToInt(TutorRating::getPuntuacion)
                .sum();
            // Redondeo al decimal más cercano
            this.ratingPromedio = Math.round((totalPuntuacion / tutor.getRatings().size()) * 10.0) / 10.0;
        } else {
            this.ratingPromedio = 0.0;
        }
    }
}