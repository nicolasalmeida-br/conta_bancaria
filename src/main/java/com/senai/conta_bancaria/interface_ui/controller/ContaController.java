package com.senai.conta_bancaria.interface_ui.controller;

import com.senai.conta_bancaria.application.dto.ContaResumoDTO;
import com.senai.conta_bancaria.application.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conta")
@RequiredArgsConstructor
public class ContaController {
    private final ContaService service;

    @GetMapping
    public ResponseEntity<List<ContaResumoDTO>> listarContasAtivas() {
        return ResponseEntity.ok(service.listarContasAtivas());
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<ContaResumoDTO> buscarContaPorNumero(@PathVariable String numero) {
        return ResponseEntity.ok(service.buscarContaPorNumero(numero));
    }

    @PutMapping("/numero/{numero}")
    public ResponseEntity<ContaResumoDTO> atualizarConta(@PathVariable String numero, @RequestBody ContaResumoDTO dto) {
        return ResponseEntity.ok(service.atualizarConta(numero, dto));
    }

    @DeleteMapping("/numero/{numero}")
    public ResponseEntity<Void> deletarConta(@PathVariable String numero) {
        service.deletarConta(numero);
        return ResponseEntity.noContent().build();
    }
}