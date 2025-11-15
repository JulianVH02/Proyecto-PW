package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.model.Flashcard;
import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.repository.FlashcardRepository;
import com.proyecto.sinergia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener todas las flashcards del usuario
    @GetMapping
    public ResponseEntity<List<Flashcard>> getMisFlashcards(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Aquí retornamos la entidad directamente por simplicidad. 
        // En un proyecto más grande usaríamos un DTO.
        // Importante: @JsonIgnore en la relación 'usuario' dentro de Flashcard o DTO para evitar bucles.
        // O simplemente hacemos esto:
        List<Flashcard> cards = flashcardRepository.findByUsuarioId(usuario.getId());
        // Limpiamos la referencia de usuario para no enviarla al frontend (opcional)
        cards.forEach(c -> c.setUsuario(null)); 
        
        return ResponseEntity.ok(cards);
    }

    // Crear nueva flashcard
    @PostMapping
    public ResponseEntity<?> crearFlashcard(@RequestBody Flashcard card, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        card.setUsuario(usuario);
        flashcardRepository.save(card);
        
        return ResponseEntity.ok("Flashcard creada exitosamente");
    }

    // Eliminar flashcard
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarFlashcard(@PathVariable Long id) {
        flashcardRepository.deleteById(id);
        return ResponseEntity.ok("Flashcard eliminada");
    }
}