package com.senai.conta_bancaria.domain.exceptions;

public class SaldoInsuficientePagamentoException extends RuntimeException {
    public SaldoInsuficientePagamentoException() {
        super("Saldo insuficiente para realizar o pagamento.");
    }
}