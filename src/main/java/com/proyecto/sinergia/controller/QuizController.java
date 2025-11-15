package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.QuizDTO;
import com.proyecto.sinergia.model.*;
import com.proyecto.sinergia.repository.QuizRepository;
import com.proyecto.sinergia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Quiz>> getMisQuizzes(Authentication auth) {
        String email = auth.getName(); // Obtiene el correo del usuario logueado
        Usuario usuario = usuarioRepository.findByCorreo(email).orElseThrow();
        return ResponseEntity.ok(quizRepository.findByUsuarioId(usuario.getId()));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> crearQuiz(@RequestBody QuizDTO quizDto, Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(email).orElseThrow();

        // Convertir DTO a Entidad
        Quiz quiz = new Quiz();
        quiz.setTitulo(quizDto.getTitulo());
        quiz.setDescripcion(quizDto.getDescripcion());
        quiz.setMateria(quizDto.getMateria());
        quiz.setUsuario(usuario);

        // Mapear preguntas y opciones
        List<Pregunta> preguntas = quizDto.getPreguntas().stream().map(pDto -> {
            Pregunta p = new Pregunta();
            p.setTextoPregunta(pDto.getTextoPregunta());
            p.setQuiz(quiz);
            
            List<Opcion> opciones = pDto.getOpciones().stream().map(oDto -> {
                Opcion o = new Opcion();
                o.setTextoOpcion(oDto.getTextoOpcion());
                o.setEsCorrecta(oDto.isEsCorrecta());
                o.setPregunta(p);
                return o;
            }).collect(Collectors.toList());
            
            p.setOpciones(opciones);
            return p;
        }).collect(Collectors.toList());

        quiz.setPreguntas(preguntas);
        quizRepository.save(quiz);

        return ResponseEntity.ok("Quiz creado exitosamente");
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarQuiz(@PathVariable Long id) {
        quizRepository.deleteById(id);
        return ResponseEntity.ok("Eliminado");
    }
}