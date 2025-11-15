package com.proyecto.sinergia.repository;

import com.proyecto.sinergia.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecursoRepository extends JpaRepository<Recurso, Long> {
    
    // Buscar todos y ordenarlos por fecha (el más nuevo primero)
    List<Recurso> findAllByOrderByFechaPublicacionDesc();
}