package com.senai.conta_bancaria.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AuthDTO {

    @Schema(
            name = "LoginRequest",
            description = "Objeto enviado no corpo da requisição de login contendo as credenciais do usuário."
    )
    public record LoginRequest(
            @Schema(
                    description = "E-mail cadastrado do usuário utilizado para autenticação.",
                    example = "admin@banco.com"
            )
            String email,

            @Schema(
                    description = "Senha do usuário correspondente ao e-mail informado.",
                    example = "admin123"
            )
            String senha
    ) {}

    @Schema(
            name = "TokenResponse",
            description = "Objeto retornado após autenticação bem-sucedida contendo o token JWT."
    )
    public record TokenResponse(
            @Schema(
                    description = "Token JWT gerado após login bem-sucedido. Deve ser utilizado no cabeçalho Authorization das requisições subsequentes.",
                    example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            String token
    ) {}
}