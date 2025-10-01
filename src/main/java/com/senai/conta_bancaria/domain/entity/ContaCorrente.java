package com.senai.conta_bancaria.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("CORRENTE")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ContaCorrente extends Conta {

    @Column(precision = 4)
    private BigDecimal limite = new BigDecimal("500.00");

    @Column(precision = 5)
    private BigDecimal taxa = new BigDecimal("0.05");

    @Override
    public String getTipo() {
        return "CORRENTE";
    }

    @Override
    public void sacar(BigDecimal valor) {
        validarValorMaiorQueZero(valor);

        BigDecimal custoSaque = valor.multiply(taxa);
        BigDecimal totalSaque = valor.add(custoSaque);

        if (getSaldo().add(limite).compareTo(totalSaque) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para o saque.");
        }
        setSaldo(getSaldo().subtract(valor));
    }
}