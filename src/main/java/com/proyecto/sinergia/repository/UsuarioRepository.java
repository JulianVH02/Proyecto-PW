package com.proyecto.sinergia.repository;

import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.model.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);
    
    List<Usuario> findByRol(RolUsuario rol);

    // --- NUEVO MÉTODO PARA ESTADÍSTICAS ---
    /**
     * Cuenta cuántos usuarios existen con un rol específico.
     * @param rol El RolUsuario (ej. ESTUDIANTE, TUTOR)
     * @return un long con el conteo total.
     */
    long countByRol(RolUsuario rol);
    
}