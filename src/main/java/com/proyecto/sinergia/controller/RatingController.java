package com.proyecto.sinergia.controller;

// ... imports existentes ...
import com.proyecto.sinergia.dto.RatingRequest;
import com.proyecto.sinergia.model.Tutor; // Importar
import com.proyecto.sinergia.model.TutorRating; // Importar
import com.proyecto.sinergia.repository.TutorRepository; // Importar
import com.proyecto.sinergia.repository.TutorRatingRepository; // Importar
import com.proyecto.sinergia.model.Recurso;
import com.proyecto.sinergia.model.RecursoRating;
import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.repository.RecursoRatingRepository;
import com.proyecto.sinergia.repository.RecursoRepository;
import com.proyecto.sinergia.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RecursoRatingRepository ratingRepository;

    @Autowired
    private RecursoRepository recursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // --- NUEVAS INYECCIONES ---
    @Autowired
    private TutorRepository tutorRepository;
    
    @Autowired
    private TutorRatingRepository tutorRatingRepository;
    // ---------------------------

    /**
     * Endpoint para calificar o actualizar la calificación de un RECURSO. (Ya existe)
     * PUT /api/ratings/recurso/{idRecurso}
     */
    @PutMapping("/recurso/{idRecurso}")
    @Transactional 
    public ResponseEntity<?> rateRecurso(@PathVariable Long idRecurso, 
                                        @RequestBody RatingRequest request, 
                                        Authentication authentication) {
        // ... (Tu lógica existente para calificar recursos) ...
        
        // Mantén el cuerpo de esta función sin cambios, ya que funciona.
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Recurso recurso = recursoRepository.findById(idRecurso)
                .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
                
        Optional<RecursoRating> existingRatingOpt = 
            ratingRepository.findByUsuarioIdAndRecursoId(usuario.getId(), idRecurso);

        RecursoRating rating;
        String mensaje;

        if (existingRatingOpt.isPresent()) {
            rating = existingRatingOpt.get();
            rating.setPuntuacion(request.getPuntuacion());
            mensaje = "Calificación de recurso actualizada con éxito.";
        } else {
            rating = new RecursoRating();
            rating.setUsuario(usuario);
            rating.setRecurso(recurso);
            rating.setPuntuacion(request.getPuntuacion());
            mensaje = "Recurso calificado con éxito.";
        }

        ratingRepository.save(rating);

        return ResponseEntity.ok(mensaje);
    }
    
    /**
     * Endpoint para que un estudiante califique o actualice la calificación de un TUTOR.
     * PUT /api/ratings/tutor/{idTutor}
     */
    @PutMapping("/tutor/{idTutor}")
    @Transactional
    public ResponseEntity<?> rateTutor(@PathVariable Long idTutor, 
                                        @RequestBody RatingRequest request, 
                                        Authentication authentication) {
        
        // 1. Obtener el estudiante autenticado
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario estudiante = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // 2. Obtener la entidad Tutor
        Tutor tutor = tutorRepository.findById(idTutor)
                .orElseThrow(() -> new RuntimeException("Tutor no encontrado"));
                
        // 3. Revisar si el estudiante ya calificó a este tutor
        Optional<TutorRating> existingRatingOpt = 
            tutorRatingRepository.findByEstudianteIdAndTutorId(estudiante.getId(), idTutor);

        TutorRating rating;
        String mensaje;

        if (existingRatingOpt.isPresent()) {
            // 4a. Actualizar calificación existente
            rating = existingRatingOpt.get();
            rating.setPuntuacion(request.getPuntuacion());
            // Nota: No actualizamos el comentario, solo la puntuación por simplicidad
            mensaje = "Calificación de tutor actualizada con éxito.";
        } else {
            // 4b. Crear nueva calificación
            rating = new TutorRating();
            rating.setEstudiante(estudiante);
            rating.setTutor(tutor);
            rating.setPuntuacion(request.getPuntuacion());
            // Nota: Podrías añadir request.getComentario() si el DTO lo tuviera
            mensaje = "Tutor calificado con éxito.";
        }

        tutorRatingRepository.save(rating);

        return ResponseEntity.ok(mensaje);
    }
}