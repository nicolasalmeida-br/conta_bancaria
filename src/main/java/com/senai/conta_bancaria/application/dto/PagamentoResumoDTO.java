package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Pagamento;
import com.senai.conta_bancaria.domain.entity.Taxa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Schema(
        name = "PagamentoResumoDTO",
        description = "Resumo das informações de um pagamento realizado, incluindo valores, status e taxas aplicadas."
)
public record PagamentoResumoDTO(

        @Schema(
                description = "Identificador único do pagamento (UUID gerado automaticamente pelo sistema).",
                example = "c9f02b1e-91df-4f5e-8b4f-82b97fd3b412"
        )
        String id,

        @Schema(
                description = "ID da conta associada ao pagamento.",
                example = "8f92afd2-414f-4ad9-95bb-0fd8b11b4b61"
        )
        @NotNull(message = "ContaId não pode ser nulo")
        String contaId,

        @Schema(
                description = "Código do boleto pago.",
                example = "34191890010104351004791020150008291070000010000"
        )
        @NotBlank(message = "O código do boleto não pode ser vazio")
        String boleto,

        @Schema(
                description = "Valor originalmente pago pelo cliente.",
                example = "150.00"
        )
        @NotNull(message = "Valor pago não pode ser nulo")
        @Digits(integer = 15, fraction = 2, message = "Valor pago deve ter até 15 dígitos inteiros e 2 decimais")
        BigDecimal valorPago,

        @Schema(
                description = "Valor final do pagamento após aplicação das taxas.",
                example = "165.75"
        )
        @NotNull(message = "Valor final não pode ser nulo")
        @Digits(integer = 15, fraction = 2, message = "Valor final deve ter até 15 dígitos inteiros e 2 decimais")
        BigDecimal valorFinal,

        @Schema(
                description = "Data e hora em que o pagamento foi realizado.",
                example = "2025-03-12T14:30:00"
        )
        @NotNull(message = "Data de pagamento não pode ser nula")
        LocalDateTime dataPagamento,

        @Schema(
                description = "Status atual do pagamento. Valores possíveis: SUCESSO, FALHA, SALDO_INSUFICIENTE, AUTENTICACAO_PENDENTE.",
                example = "SUCESSO"
        )
        @NotBlank(message = "Status não pode ser vazio")
        String status,

        @Schema(
                description = "Lista de IDs das taxas aplicadas.",
                example = "[1, 2, 5]"
        )
        Set<Long> taxasIds

) {

    public static PagamentoResumoDTO fromEntity(Pagamento pagamento) {

        Set<Long> taxas = pagamento.getTaxas() != null
                ? pagamento.getTaxas()
                .stream()
                .map(Taxa::getId)
                .collect(Collectors.toSet())
                : Set.of();

        return new PagamentoResumoDTO(
                pagamento.getId(),
                pagamento.getConta().getId(),
                pagamento.getBoleto(),
                pagamento.getValorPago(),
                pagamento.getValorFinal(),
                pagamento.getDataPagamento(),
                pagamento.getStatus().name(),
                taxas
        );
    }
}