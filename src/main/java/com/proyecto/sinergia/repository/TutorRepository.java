package com.proyecto.sinergia.repository;

import com.proyecto.sinergia.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    // (Aquí puedes añadir métodos para buscar tutores por estado, etc.)
}