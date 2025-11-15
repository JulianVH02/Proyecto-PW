package com.proyecto.sinergia.repository;

import com.proyecto.sinergia.model.TutorRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorRatingRepository extends JpaRepository<TutorRating, Long> {

    /**
     * Busca si un estudiante ya calificó a un tutor específico.
     * @param idEstudiante ID del usuario (estudiante)
     * @param idTutor ID del tutor
     * @return El rating si existe, o un Optional vacío.
     */
    Optional<TutorRating> findByEstudianteIdAndTutorId(Long idEstudiante, Long idTutor);
}