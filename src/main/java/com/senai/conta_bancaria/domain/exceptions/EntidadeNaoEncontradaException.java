package com.senai.conta_bancaria.domain.exceptions;

public class EntidadeNaoEncontradaException extends RuntimeException {
    public EntidadeNaoEncontradaException(String entidade) {
        super(entidade + " inexistente ou inativo(a).");
    }
}