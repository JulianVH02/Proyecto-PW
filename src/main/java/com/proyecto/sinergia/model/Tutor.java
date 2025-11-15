package com.proyecto.sinergia.model;

import com.proyecto.sinergia.model.enums.EstadoTutor;
// --- NUEVA IMPORTACIÓN OBLIGATORIA ---
import com.proyecto.sinergia.model.enums.EstadoTutorConverter; 
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;
import java.util.List;
import jakarta.persistence.OneToMany; // Importar

@Data
@Entity
@Table(name = "tutor")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false) 
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia") 
    private Materia materia;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String contacto;

    @Column(name = "precio_por_clase", precision = 10, scale = 2)
    private BigDecimal precioPorClase;

    private String evidencia;

    // --- CAMBIO REALIZADO AQUÍ ---
    // 1. Quitamos @Enumerated(EnumType.STRING) porque era estricto.
    // 2. Quitamos columnDefinition para dejar que JPA maneje el tipo.
    // 3. Añadimos @Convert para usar tu conversor inteligente.
    @Column(name = "estado", nullable = false)
    @Convert(converter = EstadoTutorConverter.class) 
    private EstadoTutor estado;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean verificado;
    
 // --- NUEVA RELACIÓN DE RATING ---
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorRating> ratings;
}