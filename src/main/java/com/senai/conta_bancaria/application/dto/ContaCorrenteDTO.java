package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.ContaCorrente;

public record ContaCorrenteDTO(
        Long numero,
        double saldo,
        double limite,
        double taxa
) {

    public static ContaCorrenteDTO fromEntity(ContaCorrente contaCorrente) {
        if (contaCorrente == null) return null;
        return new ContaCorrenteDTO(
                contaCorrente.getNumero(),
                contaCorrente.getSaldo(),
                contaCorrente.getLimite(),
                contaCorrente.getTaxa()
        );
    }

    public ContaCorrente toEntity() {
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setNumero(this.numero);
        contaCorrente.setSaldo(this.saldo);
        contaCorrente.setLimite(this.limite);
        contaCorrente.setTaxa(this.taxa);
        return contaCorrente;
    }
}
