package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.ContaCorrente;
import com.senai.conta_bancaria.domain.entity.ContaPoupanca;

import java.math.BigDecimal;

public record ContaResumoDTO(
        String numero,
        String tipo,
        BigDecimal saldo,
        BigDecimal limite,
        BigDecimal taxa,
        BigDecimal rendimento
) {

    public Conta toEntity(Cliente cliente) {
        if("CORRENTE".equalsIgnoreCase(tipo)) {
            return ContaCorrente.builder()
                    .cliente(cliente)
                    .numero(this.numero)
                    .saldo(this.saldo)
                    .limite(this.limite)
                    .taxa(this.taxa)
                    .ativa(true)
                    .build();
        } else if ("POUPANCA".equalsIgnoreCase(tipo)) {
            return ContaPoupanca.builder()
                    .cliente(cliente)
                    .numero(this.numero)
                    .saldo(this.saldo)
                    .rendimento(this.rendimento)
                    .ativa(true)
                    .build();
        }
        return null;
    }

    public static ContaResumoDTO fromEntity(Conta conta) {
        if (conta instanceof ContaCorrente corrente) {
            return new ContaResumoDTO(
                    conta.getNumero(),
                    conta.getTipo(),
                    conta.getSaldo(),
                    corrente.getLimite(),
                    corrente.getTaxa(),
                    null
            );
        } else if (conta instanceof ContaPoupanca poupanca) {
            return new ContaResumoDTO(
                    conta.getNumero(),
                    conta.getTipo(),
                    conta.getSaldo(),
                    null,
                    null,
                    poupanca.getRendimento()
            );
        }
        return new ContaResumoDTO(
                conta.getNumero(),
                conta.getTipo(),
                conta.getSaldo(),
                null,
                null,
                null
        );
    }
}