package com.senai.conta_bancaria.domain.exceptions;

public class PagamentoInvalidoException extends RuntimeException {
    public PagamentoInvalidoException() {
        super("O pagamento informado é inválido.");
    }
}