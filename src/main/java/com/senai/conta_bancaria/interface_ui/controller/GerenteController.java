package com.senai.conta_bancaria.interface_ui.controller;

import com.senai.conta_bancaria.application.dto.GerenteDTO;
import com.senai.conta_bancaria.application.service.GerenteService;
import com.senai.conta_bancaria.domain.entity.Gerente;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gerentes")
@RequiredArgsConstructor
public class GerenteController {

    private final GerenteService service;

    @GetMapping
    public ResponseEntity<List<GerenteDTO>> listarTodosGerentes() {
        List<GerenteDTO> gerentes = service.listarTodosGerentes();
        return ResponseEntity.ok(gerentes);
    }

    @PostMapping
    public ResponseEntity<GerenteDTO> cadastrarGerente(@RequestBody GerenteDTO dto) {
        GerenteDTO professorCriado = service.cadastrarGerente(dto);
        return ResponseEntity.ok(professorCriado);
    }

}