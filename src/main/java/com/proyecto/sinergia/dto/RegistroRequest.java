package com.proyecto.sinergia.dto;

import com.proyecto.sinergia.model.Tutor;
import com.proyecto.sinergia.model.Usuario;

// Este DTO recibe las dos entidades anidadas desde el frontend
public record RegistroRequest(Usuario usuario, Tutor tutor) {}