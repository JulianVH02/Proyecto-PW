package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.TutorResponseDto;
import com.proyecto.sinergia.model.Tutor;
import com.proyecto.sinergia.model.enums.EstadoTutor;
import com.proyecto.sinergia.repository.TutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    @Autowired
    private TutorRepository tutorRepository;

    // Endpoint público (para estudiantes logueados)
    @GetMapping("/marketplace")
    public ResponseEntity<List<TutorResponseDto>> getMarketplace() {
        // 1. Buscar solo los APROBADOS
        List<Tutor> tutoresAprobados = tutorRepository.findByEstado(EstadoTutor.APROBADO);

        // 2. Convertirlos al DTO limpio
        List<TutorResponseDto> response = tutoresAprobados.stream()
                .map(TutorResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}