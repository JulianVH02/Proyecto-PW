package com.proyecto.sinergia.repository;
import com.proyecto.sinergia.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByUsuarioId(Long usuarioId);
}