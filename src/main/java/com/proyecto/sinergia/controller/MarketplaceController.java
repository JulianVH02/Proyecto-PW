package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.TutorResponseDto; 
import com.proyecto.sinergia.model.enums.EstadoTutor;
import com.proyecto.sinergia.repository.TutorRepository; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional; // Importar
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.hibernate.Hibernate; // Importar

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceController { // Asumimos este nombre

    @Autowired
    private TutorRepository tutorRepository;

    @GetMapping("/tutores")
    @Transactional // ¡CRUCIAL! Mantiene la sesión para cargar LAZY relations
    public ResponseEntity<List<TutorResponseDto>> getTutoresAprobados() {
        
        // 1. Obtener todos los tutores aprobados
        List<TutorResponseDto> tutores = tutorRepository.findByEstado(EstadoTutor.APROBADO)
                .stream()
                // 2. Forzamos la carga de las relaciones LAZY antes de crear el DTO
                .peek(tutor -> {
                    // Carga el Usuario (necesario para nombre en el DTO)
                    Hibernate.initialize(tutor.getUsuario());
                    // Carga CRUCIAL: Inicializa la lista de ratings para el cálculo del promedio
                    Hibernate.initialize(tutor.getRatings()); 
                    // Carga la Materia (si se usa en el DTO)
                    Hibernate.initialize(tutor.getMateria()); 
                })
                // 3. Mapeamos al DTO (que ahora calcula el rating promedio)
                .map(TutorResponseDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tutores);
    }
}