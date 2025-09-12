package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;

import java.util.List;

public record ClienteDTO(
        Long id,
        String nome,
        String cpf,
        List<Long> contasIds
) {
    public static ClienteDTO fromEntity(Cliente cliente) {
        if (cliente == null) return null;
        List<Long> ids = cliente.getContas() != null
                ? cliente.getContas().stream().map(c -> c.getNumero()).toList()
                : List.of();
        return new ClienteDTO(cliente.getId(), cliente.getNome(), cliente.getCpf(), ids);
    }

    public Cliente toEntity() {
        Cliente cliente = new Cliente();
        cliente.setId(this.id);
        cliente.setNome(this.nome);
        cliente.setCpf(this.cpf);
        return cliente;
    }
}