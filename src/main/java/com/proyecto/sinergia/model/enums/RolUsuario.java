package com.proyecto.sinergia.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RolUsuario {
    ESTUDIANTE("estudiante"),
    TUTOR("tutor"),
    PENDIENTE_TUTOR("pendiente_tutor"),
    ADMIN("admin");

    private String value;

    RolUsuario(String value) {
        this.value = value;
    }

    // Jackson usará este método para serializar y deserializar (desde y hacia JSON).
    // Esto mapea la cadena "estudiante" del HTML a nuestro enum ESTUDIANTE.
    @JsonValue
    public String getValue() {
        return value;
    }
}