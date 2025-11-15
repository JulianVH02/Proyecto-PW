package com.proyecto.sinergia.model;

import com.proyecto.sinergia.model.enums.TipoRecurso;
import com.proyecto.sinergia.model.enums.TipoRecursoConverter;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "recurso")
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // La URL es obligatoria y le damos más espacio (por si son enlaces largos)
    @Column(nullable = false, length = 2048)
    private String url; 

    // Usamos el Enum con el Converter para evitar errores de texto
    @Column(name = "tipo_recurso", nullable = false)
    @Convert(converter = TipoRecursoConverter.class) 
    private TipoRecurso tipoRecurso;

    // Usamos fecha_publicacion para ser consistentes con el DTO
    @Column(name = "fecha_publicacion", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp fechaPublicacion;

    // Relación: Quién subió el recurso (Obligatorio)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Relación: A qué materia pertenece (Obligatorio)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;
    
    // --- NUEVA RELACIÓN DE RATING ---
    @OneToMany(mappedBy = "recurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecursoRating> ratings;
}