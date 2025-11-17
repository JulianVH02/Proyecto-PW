package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.RecursoRequest;
import com.proyecto.sinergia.dto.RecursoResponse;
import com.proyecto.sinergia.model.Materia;
import com.proyecto.sinergia.model.Recurso;
import com.proyecto.sinergia.model.enums.TipoRecurso;
import com.proyecto.sinergia.repository.MateriaRepository;
import com.proyecto.sinergia.repository.RecursoRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controlador de administración de recursos educativos.
 * Solo accesible para ADMIN por la regla /api/admin/** en SecurityConfig.
 */
@RestController
@RequestMapping("/api/admin/recursos")
public class AdminRecursoController {

    @Autowired
    private RecursoRepository recursoRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    /**
     * Listar todos los recursos con información completa (incluye rating promedio).
     */
    @GetMapping
    @Transactional
    public ResponseEntity<List<RecursoResponse>> getTodosLosRecursos() {
        List<RecursoResponse> recursos = recursoRepository.findAllByOrderByFechaPublicacionDesc()
                .stream()
                .peek(r -> {
                    Hibernate.initialize(r.getUsuario());
                    Hibernate.initialize(r.getMateria());
                    Hibernate.initialize(r.getRatings());
                })
                .map(RecursoResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(recursos);
    }

    /**
     * Obtener un recurso por ID.
     */
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<RecursoResponse> getRecursoPorId(@PathVariable Long id) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        Hibernate.initialize(recurso.getUsuario());
        Hibernate.initialize(recurso.getMateria());
        Hibernate.initialize(recurso.getRatings());

        return ResponseEntity.ok(new RecursoResponse(recurso));
    }

    /**
     * Actualizar un recurso existente. El admin puede cambiar título, descripción, URL,
     * tipo de recurso y materia asociada.
     */
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<RecursoResponse> actualizarRecurso(@PathVariable Long id,
                                                              @RequestBody RecursoRequest request) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));

        // Actualizar campos básicos
        recurso.setTitulo(request.getTitulo());
        recurso.setDescripcion(request.getDescripcion());
        recurso.setUrl(request.getUrl());

        // Actualizar tipo de recurso (String -> Enum)
        if (request.getTipoRecurso() != null) {
            TipoRecurso tipo = Stream.of(TipoRecurso.values())
                    .filter(c -> c.getValue().equalsIgnoreCase(request.getTipoRecurso()))
                    .findFirst()
                    .orElse(TipoRecurso.OTRO);
            recurso.setTipoRecurso(tipo);
        }

        // Actualizar materia asociada
        if (request.getMateriaId() != null) {
            Materia materia = materiaRepository.findById(request.getMateriaId())
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
            recurso.setMateria(materia);
        }

        Recurso recursoGuardado = recursoRepository.save(recurso);

        Hibernate.initialize(recursoGuardado.getUsuario());
        Hibernate.initialize(recursoGuardado.getMateria());
        Hibernate.initialize(recursoGuardado.getRatings());

        return ResponseEntity.ok(new RecursoResponse(recursoGuardado));
    }

    /**
     * Eliminar un recurso por ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRecurso(@PathVariable Long id) {
        if (!recursoRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Recurso no encontrado");
        }
        recursoRepository.deleteById(id);
        return ResponseEntity.ok("Recurso eliminado correctamente");
    }
}
