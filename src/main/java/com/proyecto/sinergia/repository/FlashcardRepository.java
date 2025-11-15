package com.proyecto.sinergia.repository;

import com.proyecto.sinergia.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
    List<Flashcard> findByUsuarioId(Long usuarioId);
}