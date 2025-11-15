package com.proyecto.sinergia.repository;

import com.proyecto.sinergia.model.Tutor;
import com.proyecto.sinergia.model.enums.EstadoTutor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    
    // Buscar tutores por estado (Para el Marketplace)
    List<Tutor> findByEstado(EstadoTutor estado);
}