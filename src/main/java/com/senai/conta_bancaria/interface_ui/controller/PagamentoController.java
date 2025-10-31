package com.senai.conta_bancaria.interface_ui.controller;

import com.senai.conta_bancaria.application.service.PagamentoAppService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    // === Início da autenticação IoT ===
    // IDs como String (UUID)
    public static record IniciarAuthRequest(String clienteId) {}
    public static record IniciarAuthResponse(Long authId, String status) {}

    @PostMapping("/iniciar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<IniciarAuthResponse> iniciar(@RequestBody IniciarAuthRequest req){
        var auth = pagamentos.iniciarAutenticacao(req.clienteId());
        return ResponseEntity.ok(new IniciarAuthResponse(auth.getId(), "AUTENTICACAO_PENDENTE"));
    }

    // === Confirmação de pagamento ===
    public static class ConfirmarPagamentoRequest {
        public String contaId;
        public String clienteId;
        public String boleto;
        public String dataVencimento;
        public BigDecimal valorPrincipal;
        public List<Long> taxaIds;
    }

    @PostMapping("/confirmar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> confirmar(@RequestBody ConfirmarPagamentoRequest req){
        return ResponseEntity.ok(
                pagamentos.confirmarPagamento(
                        req.contaId,
                        req.clienteId,
                        req.boleto,
                        (req.dataVencimento == null || req.dataVencimento.isBlank())
                                ? null
                                : LocalDate.parse(req.dataVencimento),
                        (req.valorPrincipal != null ? req.valorPrincipal : BigDecimal.ZERO),
                        req.taxaIds
                )
        );
    }
}