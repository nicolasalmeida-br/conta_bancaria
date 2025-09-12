package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.ClienteDTO;
import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteDTO salvar(ClienteDTO dto) {
        Cliente cliente = dto.toEntity();
        Cliente salvo = clienteRepository.save(cliente);
        return ClienteDTO.fromEntity(salvo);
    }

    public List<ClienteDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteDTO::fromEntity)
                .toList();
    }

    public ClienteDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow();
        return ClienteDTO.fromEntity(cliente);
    }

    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }
}