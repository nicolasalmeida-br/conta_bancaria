package com.senai.conta_bancaria.application.dto;

import java.math.BigDecimal;

public record TransferenciaDTO(
        BigDecimal valor,
        String contaDestino
) {
}