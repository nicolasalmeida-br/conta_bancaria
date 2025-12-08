package com.senai.conta_bancaria.interface_ui.exception;

import com.senai.conta_bancaria.domain.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==========================
    // REGRA NEGÓCIO: VALORES NEGATIVOS
    // ==========================
    @ExceptionHandler(ValoresNegativosException.class)
    public ProblemDetail handleValoresNegativos(
            ValoresNegativosException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Valores negativos não são permitidos",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ==========================
    // REGRA NEGÓCIO: CONTA MESMO TIPO
    // ==========================
    @ExceptionHandler(ContaMesmoTipoException.class)
    public ProblemDetail handleContaMesmoTipo(
            ContaMesmoTipoException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.CONFLICT,
                "Conta do mesmo tipo já existente",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ==========================
    // ENTIDADE NÃO ENCONTRADA (404)
    // ==========================
    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleEntidadeNaoEncontrada(
            EntidadeNaoEncontradaException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.NOT_FOUND,
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ==========================
    // SALDO INSUFICIENTE
    // ==========================
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ProblemDetail handleSaldoInsuficiente(
            SaldoInsuficienteException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Saldo insuficiente",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ==========================
    // PAGAMENTO INVÁLIDO
    // ==========================
    @ExceptionHandler(PagamentoInvalidoException.class)
    public ProblemDetail handlePagamentoInvalido(
            PagamentoInvalidoException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Pagamento inválido",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ==========================
    // RENDIMENTO INVÁLIDO
    // ==========================
    @ExceptionHandler(RendimentoInvalidoException.class)
    public ProblemDetail handleRendimentoInvalido(
            RendimentoInvalidoException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Operação de rendimento inválida",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ==========================
    // AUTENTICAÇÃO IoT EXPIRADA
    // ==========================
    @ExceptionHandler(AutenticacaoIoTExpiradaException.class)
    public ProblemDetail handleAutenticacaoIoTExpirada(
            AutenticacaoIoTExpiradaException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.UNAUTHORIZED,
                "Autenticação IoT expirada",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ==========================
    // AUTENTICAÇÃO IoT INVÁLIDA
    // ==========================
    @ExceptionHandler(AutenticacaoIoTInvalidaException.class)
    public ProblemDetail handleAutenticacaoIoTInvalida(
            AutenticacaoIoTInvalidaException ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.UNAUTHORIZED,
                "Autenticação IoT inválida",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // ==========================
    // ERROS DE VALIDAÇÃO @Valid (BODY)
    // ==========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail badRequest(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetailUtils.buildProblem(
                HttpStatus.BAD_REQUEST,
                "Erro de validação",
                "Um ou mais campos são inválidos",
                request.getRequestURI()
        );

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        problem.setProperty("errors", errors);
        return problem;
    }

    // ==========================
    // TIPO DE PARÂMETRO INVÁLIDO (ex: /api/conta/abc em vez de número)
    // ==========================
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Tipo de parâmetro inválido");
        problem.setDetail(String.format(
                "O parâmetro '%s' deve ser do tipo '%s'. Valor recebido: '%s'",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconhecido",
                ex.getValue()
        ));
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    // ==========================
    // FALHA DE CONVERSÃO (path/query/body)
    // ==========================
    @ExceptionHandler(ConversionFailedException.class)
    public ProblemDetail handleConversionFailed(
            ConversionFailedException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Falha de conversão de parâmetro");
        problem.setDetail("Um parâmetro não pôde ser convertido para o tipo esperado.");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("error", ex.getMessage());
        return problem;
    }

    // ==========================
    // VIOLAÇÃO DE CONSTRAINT (@NotNull, @Size em params etc.)
    // ==========================
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Erro de validação nos parâmetros");
        problem.setDetail("Um ou mais parâmetros são inválidos");
        problem.setInstance(URI.create(request.getRequestURI()));

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String campo = violation.getPropertyPath().toString();
            String mensagem = violation.getMessage();
            errors.put(campo, mensagem);
        });
        problem.setProperty("errors", errors);
        return problem;
    }

    // ==========================
    // EXCEÇÃO GENÉRICA (FALLBACK 500)
    // ==========================
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return ProblemDetailUtils.buildProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno no servidor",
                ex.getMessage(),
                request.getRequestURI()
        );
    }
}