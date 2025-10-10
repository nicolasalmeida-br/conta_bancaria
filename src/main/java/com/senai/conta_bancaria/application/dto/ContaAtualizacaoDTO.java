package com.senai.conta_bancaria.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ContaAtualizacaoDTO(
        @NotNull(message = "Saldo não pode ser nulo")
        @DecimalMin(value = "0.00", inclusive = true, message = "Saldo não pode ser negativo")
        @Digits(integer = 15, fraction = 2, message = "Saldo deve ter até 15 dígitos inteiros e 2 decimais")
        BigDecimal saldo,

        @NotNull(message = "Limite não pode ser nulo")
        @DecimalMin(value = "0.00", inclusive = true, message = "Limite não pode ser negativo")
        @Digits(integer = 15, fraction = 2, message = "Limite deve ter até 15 dígitos inteiros e 2 decimais")
        BigDecimal limite,

        @NotNull(message = "Rendimento não pode ser nulo")
        @DecimalMin(value = "0.00", inclusive = true, message = "Rendimento não pode ser negativo")
        @Digits(integer = 15, fraction = 2, message = "Rendimento deve ter até 15 dígitos inteiros e 2 decimais")
        BigDecimal rendimento,

        @NotNull(message = "Taxa não pode ser nula")
        @DecimalMin(value = "0.00", inclusive = true, message = "Taxa não pode ser negativa")
        @Digits(integer = 5, fraction = 4, message = "Taxa deve ter até 5 dígitos inteiros e 4 decimais")
        BigDecimal taxa
) {
}
