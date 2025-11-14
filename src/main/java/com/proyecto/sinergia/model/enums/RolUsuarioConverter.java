package com.proyecto.sinergia.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RolUsuarioConverter implements AttributeConverter<RolUsuario, String> {

    @Override
    public String convertToDatabaseColumn(RolUsuario rol) {
        if (rol == null) {
            return null;
        }
        // Guarda en la base de datos como minúsculas (ej. "estudiante")
        return rol.getValue(); 
    }

    @Override
    public RolUsuario convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        // Busca la constante Enum comparando el valor en minúsculas de la DB.
        return Stream.of(RolUsuario.values())
          .filter(c -> c.getValue().equals(dbData))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }
}