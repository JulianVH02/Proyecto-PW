package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.RecursoRequest;
import com.proyecto.sinergia.dto.RecursoResponse;
import com.proyecto.sinergia.model.Materia;
import com.proyecto.sinergia.model.Recurso;
import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.model.enums.TipoRecurso;
import com.proyecto.sinergia.repository.MateriaRepository;
import com.proyecto.sinergia.repository.RecursoRepository;
import com.proyecto.sinergia.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// --- IMPORTACIONES CRUCIALES PARA EL RATING Y LAZY LOADING ---
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate; 
// -------------------------------------------------------------

@RestController
@RequestMapping("/api/recursos")
public class RecursoController {

    @Autowired
    private RecursoRepository recursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    // Endpoint para CREAR un nuevo recurso
    @PostMapping
    // @Transactional es recomendable aquí si el Recurso tiene relaciones LAZY que se inicializan al guardar.
    @Transactional 
    public ResponseEntity<RecursoResponse> crearRecurso(@RequestBody RecursoRequest request, Authentication authentication) {
        
        // 1. Obtener el usuario autenticado (quien sube el recurso)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Buscar la materia seleccionada
        Materia materia = materiaRepository.findById(request.getMateriaId())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // 3. Convertir el String "Video" al Enum TipoRecurso.VIDEO
        TipoRecurso tipo = Stream.of(TipoRecurso.values())
          .filter(c -> c.getValue().equalsIgnoreCase(request.getTipoRecurso()))
          .findFirst()
          .orElse(TipoRecurso.OTRO);

        // 4. Crear la entidad Recurso
        Recurso nuevoRecurso = new Recurso();
        nuevoRecurso.setTitulo(request.getTitulo());
        nuevoRecurso.setDescripcion(request.getDescripcion());
        nuevoRecurso.setUrl(request.getUrl());
        nuevoRecurso.setTipoRecurso(tipo);
        nuevoRecurso.setMateria(materia);
        nuevoRecurso.setUsuario(usuario);
        nuevoRecurso.setFechaPublicacion(new Timestamp(System.currentTimeMillis()));

        Recurso recursoGuardado = recursoRepository.save(nuevoRecurso);

        // --- INICIALIZACIÓN DE RELACIONES PARA EL DTO DE RESPUESTA ---
        // Aunque no hay ratings al crear, aseguramos la carga de Materia y Usuario para el DTO.
        Hibernate.initialize(recursoGuardado.getMateria());
        Hibernate.initialize(recursoGuardado.getUsuario());
        // -------------------------------------------------------------

        // 5. Devolver el DTO de respuesta
        return ResponseEntity.ok(new RecursoResponse(recursoGuardado));
    }

    // Endpoint para OBTENER todos los recursos
    @GetMapping
    @Transactional // ¡CRUCIAL! Mantiene la sesión de Hibernate abierta para cargar ratings
    public ResponseEntity<List<RecursoResponse>> getRecursos() {
        
        List<RecursoResponse> recursos = recursoRepository.findAllByOrderByFechaPublicacionDesc()
                .stream()
                // 1. Forzamos la carga de las relaciones LAZY para evitar LazyInitializationException
                .peek(r -> {
                    // Carga el usuario y la materia (necesarios para el DTO)
                    Hibernate.initialize(r.getUsuario());
                    Hibernate.initialize(r.getMateria());
                    // CARGA CRUCIAL: Inicializa la lista de ratings antes de usarla en el DTO para el cálculo
                    Hibernate.initialize(r.getRatings()); 
                })
                .map(RecursoResponse::new) // Mapea Recurso a RecursoResponse (ahora con rating promedio)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(recursos);
    }
}