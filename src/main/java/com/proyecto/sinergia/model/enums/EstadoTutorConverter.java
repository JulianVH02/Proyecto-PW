package com.proyecto.sinergia.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class EstadoTutorConverter implements AttributeConverter<EstadoTutor, String> {

    @Override
    public String convertToDatabaseColumn(EstadoTutor estado) {
        if (estado == null) {
            return null;
        }
        // Al guardar en la BD, lo convertimos a minúsculas para mantener el estándar que ya tienes
        return estado.name().toLowerCase(); 
    }

    @Override
    public EstadoTutor convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        // ESTA ES LA CLAVE: Buscamos ignorando mayúsculas/minúsculas
        // Así "pendiente" (BD) coincidirá con PENDIENTE (Java)
        return Stream.of(EstadoTutor.values())
          .filter(e -> e.name().equalsIgnoreCase(dbData)) 
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Estado desconocido en BD: " + dbData));
    }
}