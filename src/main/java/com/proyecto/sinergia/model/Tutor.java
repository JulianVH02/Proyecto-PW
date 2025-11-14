package com.proyecto.sinergia.model;

import com.proyecto.sinergia.model.enums.EstadoTutor; // Importamos el Enum
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "tutor")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- CAMBIO CLAVE ---
    // Añadimos nullable = false para que coincida con "id_usuario INT NOT NULL"
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false) 
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia") // ON DELETE SET NULL es manejado por la BD
    private Materia materia;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String contacto;

    @Column(name = "precio_por_clase", precision = 10, scale = 2)
    private BigDecimal precioPorClase;

    private String evidencia;

    // --- CAMBIO CLAVE ---
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') DEFAULT 'PENDIENTE'")
    private EstadoTutor estado;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean verificado;
}