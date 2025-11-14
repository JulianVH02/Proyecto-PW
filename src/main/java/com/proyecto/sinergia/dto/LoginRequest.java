package com.proyecto.sinergia.dto;

// Un 'record' es una forma moderna de crear clases inmutables
public record LoginRequest(String correo, String contraseña) {}