package com.senai.conta_bancaria.interface_ui.exception;

import com.senai.conta_bancaria.domain.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ContaMesmoTipoException
    @ExceptionHandler(ContaMesmoTipoException.class)
    public ProblemDetail handleContaMesmoTipo(ContaMesmoTipoException ex,
                                              HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Tipo de conta inválido para operação.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // EntidadeNaoEncontradaException
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException ex,
                                                     HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // RendimentoInvalidoException
    @ExceptionHandler(RendimentoInvalidoException.class)
    public ProblemDetail handleRendimentoInvalido(RendimentoInvalidoException ex,
                                                  HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Rendimento inválido.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // SaldoInsuficienteException
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ProblemDetail handleSaldoInsuficiente(SaldoInsuficienteException ex,
                                                 HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.FORBIDDEN,
                "Saldo insuficiente para realizar a operação.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // TipoDeContaInvalidaException
    @ExceptionHandler(TipoDeContaInvalidaException.class)
    public ProblemDetail handleTipoDeContaInvalida(TipoDeContaInvalidaException ex,
                                                   HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Tipo de conta inválido.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // TransferenciaParaMesmaContaException
    @ExceptionHandler(TransferenciaParaMesmaContaException.class)
    public ProblemDetail handleTransferenciaParaMesmaConta(TransferenciaParaMesmaContaException ex,
                                                           HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Transferência para a mesma conta não é permitida.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ValoresNegativosException
    @ExceptionHandler(ValoresNegativosException.class)
    public ProblemDetail handleValoresNegativo(ValoresNegativosException ex,
                                               HttpServletRequest request) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Valores negativos não são permitidos.",
                ex.getMessage(),
                request.getRequestURI()
        );
    }
}