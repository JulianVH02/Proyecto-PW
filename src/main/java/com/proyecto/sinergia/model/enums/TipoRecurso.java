package com.proyecto.sinergia.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoRecurso {
    VIDEO("Video"),
    ARTICULO("Artículo"),
    GUIA_PDF("Guía/PDF"),
    SIMULADOR("Simulador"),
    OTRO("Otro");

    private final String value;

    TipoRecurso(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}