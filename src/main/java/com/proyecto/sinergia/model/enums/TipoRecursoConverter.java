package com.proyecto.sinergia.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class TipoRecursoConverter implements AttributeConverter<TipoRecurso, String> {

    @Override
    public String convertToDatabaseColumn(TipoRecurso tipo) {
        if (tipo == null) return null;
        // Guardamos el valor legible (ej. "Video")
        return tipo.getValue(); 
    }

    @Override
    public TipoRecurso convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        
        return Stream.of(TipoRecurso.values())
          .filter(c -> c.getValue().equalsIgnoreCase(dbData))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Tipo de recurso desconocido: " + dbData));
    }
}