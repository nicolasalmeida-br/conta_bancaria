package com.senai.conta_bancaria.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(
        name = "ContaAtualizacaoDTO",
        description = "Objeto utilizado para atualização parcial dos dados financeiros da conta."
)
public record ContaAtualizacaoDTO(

        @Schema(
                description = "Saldo da conta. Se não enviado, permanece o valor atual.",
                example = "1500.75"
        )
        BigDecimal saldo,

        @Schema(
                description = "Limite da conta. Se não enviado, permanece o valor atual.",
                example = "500.00"
        )
        BigDecimal limite,

        @Schema(
                description = "Rendimento da conta. Se não enviado, permanece o valor atual.",
                example = "25.45"
        )
        BigDecimal rendimento,

        @Schema(
                description = "Taxa da conta em porcentagem decimal.",
                example = "0.015"
        )
        BigDecimal taxa
) {}