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
        // Siempre guardamos en minúsculas para mantener el estándar
        return rol.getValue(); 
    }

    @Override
    public RolUsuario convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        // --- CORRECCIÓN AQUÍ ---
        // Usamos 'equalsIgnoreCase' para que no importe si en la BD 
        // está guardado como "ESTUDIANTE" o "estudiante".
        return Stream.of(RolUsuario.values())
          .filter(c -> c.getValue().equalsIgnoreCase(dbData)) 
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Rol desconocido en BD: " + dbData));
    }
}