package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Gerente;
import com.senai.conta_bancaria.domain.enums.Role;
import lombok.Builder;

@Builder
public record GerenteDTO(
        String id,
        String nome,
        String cpf,
        String email,
        String senha,
        Boolean ativo,
        Role role
) {
    public static GerenteDTO fromEntity(Gerente gerente) {
        return GerenteDTO.builder()
                .id(gerente.getId())
                .nome(gerente.getNome())
                .cpf(gerente.getCpf())
                .email(gerente.getEmail())
                .ativo(gerente.isAtivo())
                .role(gerente.getRole())
                .build();
    }

    public Gerente toEntity() {
        return Gerente.builder()
                .id(this.id)
                .nome(this.nome)
                .cpf(this.cpf)
                .email(this.email)
                .senha(this.senha)
                .ativo(this.ativo != null ? this.ativo : true)
                .role(this.role != null ? this.role : Role.CLIENTE)
                .build();
    }

}