package com.senai.conta_bancaria.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class AutenticacaoIoTInvalidaException extends RuntimeException {

    public AutenticacaoIoTInvalidaException() {
        super("A autenticação IoT falhou. Código inválido ou não autorizado.");
    }

    public AutenticacaoIoTInvalidaException(String mensagem) {
        super(mensagem);
    }

    public ProblemDetail toProblemDetail() {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Autenticação IoT inválida");
        pd.setDetail(getMessage());
        pd.setProperty("codigo", "IOT_INVALID");
        return pd;
    }
}