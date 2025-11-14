package com.proyecto.sinergia.repository;

import com.proyecto.sinergia.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Spring Data JPA crea la consulta: "SELECT * FROM usuario WHERE correo = ?"
    Optional<Usuario> findByCorreo(String correo);
}