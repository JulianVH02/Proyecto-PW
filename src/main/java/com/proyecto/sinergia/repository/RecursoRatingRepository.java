package com.proyecto.sinergia.repository;

import com.proyecto.sinergia.model.RecursoRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecursoRatingRepository extends JpaRepository<RecursoRating, Long> {

    /**
     * Busca si un usuario específico ya calificó un recurso específico.
     * @param idUsuario ID del usuario
     * @param idRecurso ID del recurso
     * @return El rating si existe, o un Optional vacío.
     */
    Optional<RecursoRating> findByUsuarioIdAndRecursoId(Long idUsuario, Long idRecurso);
}