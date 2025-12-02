package com.senai.conta_bancaria.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(
        name = "TaxaDTO",
        description = "Objeto utilizado para cadastrar ou atualizar informações de taxas aplicadas ao sistema bancário."
)
public record TaxaDTO(

        @Schema(
                description = "Descrição da taxa (ex: 'Manutenção mensal', 'Juros diário').",
                example = "Tarifa de manutenção"
        )
        @NotBlank(message = "Descrição não pode ser vazia")
        String descricao,

        @Schema(
                description = "Percentual da taxa aplicada. Representado como valor decimal (ex: 0.02 = 2%). Pode ser zero.",
                example = "0.015"
        )
        @NotNull(message = "Percentual não pode ser nulo")
        @DecimalMin(value = "0.00", inclusive = true, message = "Percentual não pode ser negativo")
        @Digits(integer = 5, fraction = 4, message = "Percentual deve ter até 5 dígitos inteiros e 4 decimais")
        BigDecimal percentual,

        @Schema(
                description = "Valor fixo da taxa (ex: tarifa bancária). Pode ser zero.",
                example = "12.50"
        )
        @NotNull(message = "Valor fixo não pode ser nulo")
        @DecimalMin(value = "0.00", inclusive = true, message = "Valor fixo não pode ser negativo")
        @Digits(integer = 15, fraction = 2, message = "Valor fixo deve ter até 15 dígitos inteiros e 2 decimais")
        BigDecimal valorFixo
) {}