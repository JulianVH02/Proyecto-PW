package com.proyecto.sinergia.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "materia")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;
    
    // (Aquí iría un @OneToMany si quisieras que la materia 
    // conozca a todos sus tutores, pero no es necesario por ahora)
}