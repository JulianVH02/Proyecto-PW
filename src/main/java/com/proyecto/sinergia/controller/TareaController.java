package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.TareaDTO;
import com.proyecto.sinergia.model.Tarea;
import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.repository.TareaRepository;
import com.proyecto.sinergia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Obtener tareas del usuario logueado
    @GetMapping
    public ResponseEntity<List<TareaDTO>> getMisTareas(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<TareaDTO> eventos = tareaRepository.findByUsuarioId(usuario.getId())
                .stream()
                .map(TareaDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(eventos);
    }

    // Crear nueva tarea
    @PostMapping
    public ResponseEntity<?> crearTarea(@RequestBody Tarea tarea, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        tarea.setUsuario(usuario);
        // Si no viene color, ponemos uno por defecto
        if (tarea.getColor() == null || tarea.getColor().isEmpty()) {
            tarea.setColor("#3788d8"); 
        }
        
        tareaRepository.save(tarea);
        return ResponseEntity.ok("Tarea guardada");
    }
    
    // Eliminar tarea
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTarea(@PathVariable Long id) {
        tareaRepository.deleteById(id);
        return ResponseEntity.ok("Tarea eliminada");
    }
}