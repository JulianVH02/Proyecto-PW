package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.TutorProfileResponse;
import com.proyecto.sinergia.dto.TutorResponseDto;
import com.proyecto.sinergia.model.Tutor;
import com.proyecto.sinergia.model.enums.EstadoTutor;
import com.proyecto.sinergia.repository.TutorRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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

    // Perfil detallado de un tutor: incluye comentarios
    @GetMapping("/{id}/perfil")
    @Transactional
    public ResponseEntity<TutorProfileResponse> getPerfilTutor(@PathVariable Long id) {
        Optional<Tutor> tutorOpt = tutorRepository.findById(id);
        if (tutorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Tutor tutor = tutorOpt.get();

        // Inicializar relaciones LAZY necesarias para el DTO
        Hibernate.initialize(tutor.getUsuario());
        Hibernate.initialize(tutor.getMateria());
        Hibernate.initialize(tutor.getRatings());

        TutorProfileResponse dto = new TutorProfileResponse(tutor);
        return ResponseEntity.ok(dto);
    }
}