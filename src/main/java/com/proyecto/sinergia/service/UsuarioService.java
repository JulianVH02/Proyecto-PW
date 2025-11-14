package com.proyecto.sinergia.service;

import com.proyecto.sinergia.model.Materia;
import com.proyecto.sinergia.model.Tutor;
import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.model.enums.EstadoTutor; // Importar Enums
import com.proyecto.sinergia.model.enums.RolUsuario;  // Importar Enums
import com.proyecto.sinergia.repository.MateriaRepository;
import com.proyecto.sinergia.repository.TutorRepository;
import com.proyecto.sinergia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private MateriaRepository materiaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrarUsuario(Usuario usuario, Tutor tutorInfo) {
        
        // 1. Hashear contraseña
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        
        // --- CAMBIO CLAVE: Usamos Enums para comparar ---
        
        if (RolUsuario.ESTUDIANTE.equals(usuario.getRol())) {
            usuario.setActivo(true);
            // El rol ya viene como Enum desde el DTO, Spring lo convierte
            return usuarioRepository.save(usuario); 
        }
        
        if (RolUsuario.TUTOR.equals(usuario.getRol())) {
            // 2. Poner el rol pendiente
            usuario.setRol(RolUsuario.PENDIENTE_TUTOR);
            usuario.setActivo(true); 
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            
            // 3. Asociar usuario y estado al tutor
            tutorInfo.setUsuario(usuarioGuardado);
            tutorInfo.setEstado(EstadoTutor.PENDIENTE); // Usamos Enum
            tutorInfo.setVerificado(false);
            
            // 4. Manejar la asociación de Materia
            if (tutorInfo.getMateria() != null && tutorInfo.getMateria().getId() != null) {
                Materia materia = materiaRepository.findById(tutorInfo.getMateria().getId())
                        .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
                tutorInfo.setMateria(materia);
            } else {
                 throw new RuntimeException("El tutor debe tener una materia asociada.");
            }

            tutorRepository.save(tutorInfo);
            
            return usuarioGuardado;
        }

        throw new IllegalArgumentException("Rol de usuario no válido.");
    }
    
    public java.util.Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }
}