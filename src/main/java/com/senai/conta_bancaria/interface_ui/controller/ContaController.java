package com.senai.conta_bancaria.interface_ui.controller;

import com.senai.conta_bancaria.application.service.ContaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contas")
public class ContaController {
    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping("/{id}/depositar")
    public void depositar(@PathVariable Long id, @RequestParam Double valor) {
        contaService.depositar(id, valor);
    }

    @PostMapping("/{id}/sacar")
    public void sacar(@PathVariable Long id, @RequestParam Double valor) {
        contaService.sacar(id, valor);
    }
}