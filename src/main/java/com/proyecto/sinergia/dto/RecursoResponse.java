package com.proyecto.sinergia.dto;

import com.proyecto.sinergia.model.Recurso;
import com.proyecto.sinergia.model.RecursoRating; // IMPORTAR
import lombok.Data;
import java.sql.Timestamp;

@Data
public class RecursoResponse {
    private Long id;
    private String titulo;
    private String url;
    private String descripcion;
    private String tipoRecurso;
    private Timestamp fechaPublicacion;
    private String materiaNombre;
    private String usuarioNombre;
    
    // --- NUEVO CAMPO DE RATING ---
    private Double ratingPromedio; 
    // -----------------------------

    // Constructor para mapear fácil
    public RecursoResponse(Recurso recurso) {
        this.id = recurso.getId();
        this.titulo = recurso.getTitulo();
        this.url = recurso.getUrl();
        this.descripcion = recurso.getDescripcion();
        this.tipoRecurso = recurso.getTipoRecurso().getValue();
        this.fechaPublicacion = recurso.getFechaPublicacion();
        this.materiaNombre = recurso.getMateria().getNombre();
        
        // Asumimos que el Usuario y la Materia están cargados
        this.usuarioNombre = recurso.getUsuario().getNombre() + " " + recurso.getUsuario().getApellido();
        
        // CALCULAMOS EL RATING PROMEDIO AQUÍ
        if (recurso.getRatings() != null && !recurso.getRatings().isEmpty()) {
            double totalPuntuacion = recurso.getRatings().stream()
                .mapToInt(RecursoRating::getPuntuacion)
                .sum();
            // Usamos Math.round para redondear a un decimal más amigable
            this.ratingPromedio = Math.round((totalPuntuacion / recurso.getRatings().size()) * 10.0) / 10.0;
        } else {
            this.ratingPromedio = 0.0;
        }
    }
}