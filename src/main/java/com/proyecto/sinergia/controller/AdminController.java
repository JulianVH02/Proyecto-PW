package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.model.Tutor;
import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.model.enums.EstadoTutor;
import com.proyecto.sinergia.model.enums.RolUsuario;
import com.proyecto.sinergia.repository.TutorRepository;
import com.proyecto.sinergia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Imports necesarios para el nuevo método
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TutorRepository tutorRepository;

    // --- NUEVO ENDPOINT DE ESTADÍSTICAS ---
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getEstadisticas() {
        // Usamos el nuevo método del repositorio
        long totalEstudiantes = usuarioRepository.countByRol(RolUsuario.ESTUDIANTE);
        long totalTutores = usuarioRepository.countByRol(RolUsuario.TUTOR);
        long totalPendientes = usuarioRepository.countByRol(RolUsuario.PENDIENTE_TUTOR);

        // Creamos un mapa para la respuesta JSON
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalEstudiantes", totalEstudiantes);
        stats.put("totalTutores", totalTutores);
        stats.put("totalPendientes", totalPendientes);

        return ResponseEntity.ok(stats);
    }

    // --- Métodos existentes (sin cambios) ---

    @GetMapping("/pendientes")
    public ResponseEntity<List<Map<String, Object>>> getPendientes() {
        List<Usuario> pendientes = usuarioRepository.findByRol(RolUsuario.PENDIENTE_TUTOR);

        List<Map<String, Object>> respuesta = pendientes.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("nombre", u.getNombre());
            map.put("apellido", u.getApellido());
            map.put("correo", u.getCorreo());
            
            if (u.getTutor() != null) {
                map.put("evidencia", u.getTutor().getEvidencia());
                if (u.getTutor().getMateria() != null) {
                    map.put("materia", u.getTutor().getMateria().getNombre());
                }
            }
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/aprobar/{id}")
    public ResponseEntity<?> aprobarTutor(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (usuario.getRol() == RolUsuario.PENDIENTE_TUTOR) {
            usuario.setRol(RolUsuario.TUTOR);
            usuarioRepository.save(usuario);

            if (usuario.getTutor() != null) {
                Tutor tutor = usuario.getTutor();
                tutor.setEstado(EstadoTutor.APROBADO);
                tutor.setVerificado(true);
                tutorRepository.save(tutor);
            }
            return ResponseEntity.ok("Tutor aprobado correctamente");
        }
        return ResponseEntity.badRequest().body("El usuario no está pendiente de aprobación");
    }

    @PostMapping("/rechazar/{id}")
    public ResponseEntity<?> rechazarTutor(@PathVariable Long id) {
        
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // 1. Cambiar el Rol del Usuario a ESTUDIANTE
        usuario.setRol(RolUsuario.ESTUDIANTE);
        
        if (usuario.getTutor() != null) {
            Tutor tutor = usuario.getTutor();
            
            // 2. IMPORTANTE: Cambiar el estado de la solicitud a RECHAZADO
            // Esto mantiene el registro histórico en la tabla 'tutor'
            tutor.setEstado(EstadoTutor.RECHAZADO);
            tutor.setVerificado(false); 
            
            tutorRepository.save(tutor); // Guardar el cambio de estado en la tabla 'tutor'
        }
        
        usuarioRepository.save(usuario); // Guardar el cambio de rol en la tabla 'usuario'
        
        return ResponseEntity.ok("Solicitud rechazada. El usuario volvió a ser Estudiante y la solicitud se marcó como RECHAZADA.");
    }
}