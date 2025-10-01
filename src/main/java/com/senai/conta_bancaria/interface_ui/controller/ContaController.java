package com.senai.conta_bancaria.interface_ui.controller;

import com.senai.conta_bancaria.application.dto.ContaAtualizacaoDTO;
import com.senai.conta_bancaria.application.dto.ContaResumoDTO;
import com.senai.conta_bancaria.application.dto.TransferenciaDTO;
import com.senai.conta_bancaria.application.dto.ValorSaqueDepositoDTO;
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

    @GetMapping("/{numero}")
    public ResponseEntity<ContaResumoDTO> buscarContaPorNumero(@PathVariable String numero) {
        return ResponseEntity.ok(service.buscarContaPorNumero(numero));
    }

    @PutMapping("/{numero}")
    public ResponseEntity<ContaResumoDTO> atualizarConta(@PathVariable String numero, @RequestBody ContaAtualizacaoDTO dto) {
        return ResponseEntity.ok(service.atualizarConta(numero, dto));
    }

    @DeleteMapping("/{numero}")
    public ResponseEntity<Void> deletarConta(@PathVariable String numero) {
        service.deletarConta(numero);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{numero}/sacar")
    public ResponseEntity<ContaResumoDTO> sacar(@PathVariable String numero, @RequestBody ValorSaqueDepositoDTO dto) {
        return ResponseEntity.ok(service.sacar(numero, dto));
    }

    @PostMapping("/{numero}/depositar")
    public ResponseEntity<ContaResumoDTO> depositar(@PathVariable String numero, @RequestBody ValorSaqueDepositoDTO dto) {
        return ResponseEntity.ok(service.depositar(numero, dto));
    }

    @PostMapping("/{numero}/transferir")
    public ResponseEntity<ContaResumoDTO> transferir(@PathVariable String numero, @RequestBody TransferenciaDTO dto) {
       return ResponseEntity.ok(service.transferir(numero, dto));
    }
}