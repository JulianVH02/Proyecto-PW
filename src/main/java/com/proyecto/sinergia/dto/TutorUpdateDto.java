package com.proyecto.sinergia.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TutorUpdateDto {
    private String descripcion;
    private String contacto;
    private BigDecimal precioPorClase;
}