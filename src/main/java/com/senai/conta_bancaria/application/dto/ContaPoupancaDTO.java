package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.ContaPoupanca;
import java.math.BigDecimal;

public record ContaPoupancaDTO(
        String numero,
        BigDecimal saldo,
        BigDecimal rendimento
) {

    public static ContaPoupancaDTO fromEntity(ContaPoupanca contaPoupanca) {
        if (contaPoupanca == null) return null;
        return new ContaPoupancaDTO(
                contaPoupanca.getNumero(),
                contaPoupanca.getSaldo(),
                contaPoupanca.getRendimento()
        );
    }

    public ContaPoupanca toEntity() {
        ContaPoupanca contaPoupanca = new ContaPoupanca();
        contaPoupanca.setNumero(this.numero);
        contaPoupanca.setSaldo(this.saldo);
        contaPoupanca.setRendimento(this.rendimento);
        return contaPoupanca;
    }
}