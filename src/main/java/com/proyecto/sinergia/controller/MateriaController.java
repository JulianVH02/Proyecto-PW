package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.MateriaDTO;
import com.proyecto.sinergia.service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/materias") // Endpoint público
public class MateriaController {

    @Autowired
    private MateriaService materiaService;

    /**
     * Endpoint público para que CUALQUIERA (incluida la página de registro)
     * pueda ver la lista de materias disponibles.
     */
    @GetMapping
    public ResponseEntity<List<MateriaDTO>> getAllMaterias() {
        List<MateriaDTO> materias = materiaService.getMaterias();
        return ResponseEntity.ok(materias);
    }
}