package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.TutorUpdateDto;
import com.proyecto.sinergia.model.Tutor;
import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.model.enums.EstadoTutor;
import com.proyecto.sinergia.repository.TutorRepository;
import com.proyecto.sinergia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate; // Necesaria para la inicialización forzada

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/tutor")
public class TutorManagementController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private TutorRepository tutorRepository;

    // Clase interna para enviar el estado del perfil al frontend
    class TutorStatus {
        public boolean approved;
        public boolean profileComplete;
        public String nombre;
        public String materia;
        public String descripcion;
        public String contacto;
        public BigDecimal precioPorClase;
        
        public TutorStatus(Tutor t, Usuario u, boolean complete) {
            this.approved = t.getEstado() == EstadoTutor.APROBADO;
            this.profileComplete = complete;
            this.nombre = u.getNombre() + " " + u.getApellido();
         // --- CORRECCIÓN DE SEGURIDAD CONTRA NULLPOINTEREXCEPTION ---
            // Verifica si la Materia existe antes de intentar obtener el nombre
            this.materia = (t.getMateria() != null && t.getMateria().getNombre() != null) 
                           ? t.getMateria().getNombre() 
                           : "No Asignada"; 
            // ----------------------------------------------------------
            this.materia = (t.getMateria() != null) ? t.getMateria().getNombre() : "General";
            this.descripcion = t.getDescripcion();
            this.contacto = t.getContacto();
            this.precioPorClase = t.getPrecioPorClase();
        }
    }

    /**
     * [GET] Endpoint para que el Frontend verifique el estado del tutor (Aprobado y Perfil Completo).
     */
    @GetMapping("/profile")
    @Transactional // Dejamos @Transactional y la importación de Hibernate
    public ResponseEntity<?> getTutorProfile(Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        // 1. Verificar si el tutor existe y está APROBADO (Requisito 1)
        if (usuario.getTutor() == null || usuario.getTutor().getEstado() != EstadoTutor.APROBADO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tu solicitud aún no ha sido aprobada por el administrador.");
        }
        
        Tutor tutor = usuario.getTutor();
        
        // --- Solución Lazy Loading ---
        // Aseguramos la inicialización de relaciones LAZY
        Hibernate.initialize(tutor.getMateria()); 
        Hibernate.initialize(tutor.getUsuario());
        // ------------------------------
        
        // 2. VERIFICACIÓN ROBUSTA DE PERFIL COMPLETO
        // Hacemos que la comprobación del precio sea también robusta a null antes de la comparación BigDecimal
        boolean datosCompletos = tutor.getDescripcion() != null && !tutor.getDescripcion().trim().isEmpty() &&
                                 tutor.getContacto() != null && !tutor.getContacto().trim().isEmpty() &&
                                 // ¡CORRECCIÓN! El precio debe ser un Optional antes de usar compareTo
                                 tutor.getPrecioPorClase() != null && tutor.getPrecioPorClase().compareTo(BigDecimal.ZERO) > 0;
        
        return ResponseEntity.ok(new TutorStatus(tutor, usuario, datosCompletos));
    }

    /**
     * [POST] Endpoint para que el tutor complete/actualice su perfil.
     */
    @PostMapping("/profile/update")
    public ResponseEntity<?> updateTutorProfile(@RequestBody TutorUpdateDto dto, Principal principal) {
        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByCorreo(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        if (usuario.getTutor() == null || usuario.getTutor().getEstado() != EstadoTutor.APROBADO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para actualizar este perfil.");
        }
        
        Tutor tutor = usuario.getTutor();
        
        // Actualizar datos
        tutor.setDescripcion(dto.getDescripcion());
        tutor.setContacto(dto.getContacto());
        // El precioPorClase se recibe como BigDecimal en el DTO
        tutor.setPrecioPorClase(dto.getPrecioPorClase());
        
        tutorRepository.save(tutor);
        
        return ResponseEntity.ok("Perfil de tutor actualizado con éxito.");
    }
}