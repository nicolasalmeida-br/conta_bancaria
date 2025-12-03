package com.senai.conta_bancaria.interface_ui.controller;

import com.senai.conta_bancaria.application.dto.PagamentoResumoDTO;
import com.senai.conta_bancaria.application.service.PagamentoAppService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class PagamentoController {

    private final PagamentoAppService pagamentos;

    public PagamentoController(PagamentoAppService pagamentos) {
        this.pagamentos = pagamentos;
    }

    @PostMapping("/autenticacao")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> iniciarAutenticacao(@RequestBody IniciarAutenticacaoRequest req) {
        return ResponseEntity.ok(pagamentos.iniciarAutenticacao(req.clienteId));
    }

    @PostMapping("/confirmar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PagamentoResumoDTO> confirmar(@RequestBody ConfirmarPagamentoRequest req) {

        LocalDate vencimento = (req.dataVencimento == null || req.dataVencimento.isBlank())
                ? null
                : LocalDate.parse(req.dataVencimento);

        PagamentoResumoDTO dto = pagamentos.confirmarPagamento(
                req.contaId,
                req.clienteId,
                req.boleto,
                vencimento,
                req.valorPrincipal,
                req.taxaIds
        );

        return ResponseEntity.ok(dto);
    }

    // DTOs internos de request

    public static class IniciarAutenticacaoRequest {
        @NotBlank public String clienteId;
    }

    public static class ConfirmarPagamentoRequest {
        @NotBlank public String contaId;
        @NotBlank public String clienteId;
        @NotBlank public String boleto;
        public String dataVencimento;
        @NotNull public BigDecimal valorPrincipal;
        public List<Long> taxaIds;
    }
}