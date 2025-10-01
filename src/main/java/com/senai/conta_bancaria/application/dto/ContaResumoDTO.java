package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.ContaCorrente;
import com.senai.conta_bancaria.domain.entity.ContaPoupanca;

import java.math.BigDecimal;

public record ContaResumoDTO(
        String numero,
        String tipo,
        BigDecimal saldo
) {
    public Conta toEntity(Cliente cliente){
        if("CORRENTE".equalsIgnoreCase(tipo)){
            return ContaCorrente.builder()
                    .numero(this.numero)
                    .saldo(this.saldo)
                    .ativa(true)
                    .cliente(cliente)
                    .build();
        } else if ("POUPANCA".equalsIgnoreCase(tipo)){
            return ContaPoupanca.builder()
                    .numero(this.numero)
                    .saldo(this.saldo)
                    .ativa(true)
                    .cliente(cliente)
                    .build();
        }
        return null;
    }
    public static ContaResumoDTO fromEntity(Conta c) {
        return new ContaResumoDTO(
                c.getNumero(),
                c.getTipo(),
                c.getSaldo()
        );
    }
}