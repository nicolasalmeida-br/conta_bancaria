package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;

public record ClienteRegistroDTO(
        @NotBlank(message = "Nome é obrigatório.")
        String nome,

        @NotBlank
        String cpf,

        @NotBlank
        String email,

        @NotBlank
        String senha,

        @Valid
        ContaResumoDTO contaDTO
) {
    public Cliente toEntity() {
        return Cliente.builder()
                .ativo(true)
                .nome(this.nome)
                .cpf(this.cpf)
                .email(this.email)
                .senha(this.senha)
                .role(Role.CLIENTE)
                .contas(new ArrayList<>())
                .build();
    }
}
