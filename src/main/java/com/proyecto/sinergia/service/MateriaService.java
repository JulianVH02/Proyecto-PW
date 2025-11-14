package com.proyecto.sinergia.service;

import com.proyecto.sinergia.dto.MateriaDTO;
import com.proyecto.sinergia.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository materiaRepository;

    /**
     * Obtiene todas las materias de la base de datos y las convierte a DTO.
     * @return Lista de MateriaDTO.
     */
    public List<MateriaDTO> getMaterias() {
        // 1. Busca todas las entidades Materia
        return materiaRepository.findAll()
                // 2. Convierte cada Materia a MateriaDTO usando un stream
                .stream()
                .map(materia -> new MateriaDTO(materia.getId(), materia.getNombre()))
                // 3. Devuelve la lista de DTOs
                .collect(Collectors.toList());
    }
}