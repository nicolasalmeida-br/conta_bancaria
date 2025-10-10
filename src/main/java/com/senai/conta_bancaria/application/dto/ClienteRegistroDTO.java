package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;
import jakarta.validation.Valid;
import org.hibernate.validator.constraints.br.CPF;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;

public record ClienteRegistroDTO(
        @NotBlank(message = "Nome é obrigatório.")
        String nome,

        @CPF(message = "O CPF fornecido não é válido.")
        String cpf,

        @Valid
        ContaResumoDTO contaDTO
) {
    public Cliente toEntity() {
        return Cliente.builder()
                .ativo(true)
                .nome(this.nome)
                .cpf(this.cpf)
                .contas(new ArrayList<>())
                .build();
    }
}
