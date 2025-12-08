package com.senai.conta_bancaria.domain.exceptions;

public class AutenticacaoIoTExpiradaException extends RuntimeException {

    public AutenticacaoIoTExpiradaException() {
        super("A autenticação IoT expirou. Solicite um novo código no dispositivo.");
    }

    public AutenticacaoIoTExpiradaException(String mensagem) {
        super(mensagem);
    }
}