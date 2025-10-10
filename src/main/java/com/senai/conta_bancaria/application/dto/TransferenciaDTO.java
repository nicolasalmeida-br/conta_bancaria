package com.senai.conta_bancaria.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferenciaDTO(

        @DecimalMin(value = "0.01", inclusive = true, message = "O valor da transferência deve ser maior que zero")
        @Digits(integer = 15, fraction = 2, message = "O valor da transferência deve ter até 15 dígitos inteiros e 2 decimais")
        BigDecimal valor,

        @NotBlank(message = "A conta destino não pode ser vazia")
        @Size(min = 6, max = 20, message = "O número da conta destino deve ter entre 6 e 20 caracteres")
        @Pattern(regexp = "^[0-9]+$", message = "O número da conta destino deve conter apenas números")
        String contaDestino
) {
}