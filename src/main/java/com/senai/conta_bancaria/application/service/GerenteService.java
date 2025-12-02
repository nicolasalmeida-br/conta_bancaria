package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.GerenteDTO;
import com.senai.conta_bancaria.domain.entity.Gerente;
import com.senai.conta_bancaria.domain.enums.Role;
import com.senai.conta_bancaria.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.conta_bancaria.domain.repository.GerenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável pelo gerenciamento de gerentes.
 * Inclui operações de listagem, cadastro, busca por ID, atualização e remoção.
 */
@Service
@RequiredArgsConstructor
public class GerenteService {

    private final GerenteRepository gerenteRepository;
    private final PasswordEncoder encoder;

    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public List<GerenteDTO> listarTodosGerentes() {
        return gerenteRepository.findAll().stream()
                .map(GerenteDTO::fromEntity)
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public GerenteDTO cadastrarGerente(GerenteDTO dto) {
        Gerente entity = dto.toEntity();

        // Criptografa a senha antes de salvar
        entity.setSenha(encoder.encode(dto.senha()));

        // Define o role padrão
        entity.setRole(Role.CLIENTE);

        gerenteRepository.save(entity);

        return GerenteDTO.fromEntity(entity);
    }

    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    public GerenteDTO buscarGerentePorId(String id) {
        Gerente gerente = gerenteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Gerente"));
        return GerenteDTO.fromEntity(gerente);
    }

    /**
     * Atualiza os dados de um gerente existente.
     *
     * @param id  ID do gerente
     * @param dto DTO com os novos dados
     * @return DTO do gerente atualizado
     * @throws EntidadeNaoEncontradaException se o gerente não existir
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    public GerenteDTO atualizarGerente(String id, GerenteDTO dto) {
        Gerente gerente = gerenteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Gerente"));

        gerente.setNome(dto.nome());
        gerente.setCpf(dto.cpf());
        gerente.setEmail(dto.email());

        // Atualiza senha apenas se fornecida
        if (dto.senha() != null && !dto.senha().isBlank()) {
            gerente.setSenha(encoder.encode(dto.senha()));
        }

        // Atualiza o role caso fornecido
        if (dto.role() != null) {
            gerente.setRole(dto.role());
        }

        gerenteRepository.save(gerente);

        return GerenteDTO.fromEntity(gerente);
    }

    /**
     * Remove (desativa) um gerente pelo seu ID.
     *
     * @param id ID do gerente
     * @throws EntidadeNaoEncontradaException se o gerente não existir
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deletarGerente(String id) {
        Gerente gerente = gerenteRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Gerente"));

        // Desativa o gerente
        gerente.setAtivo(false);

        gerenteRepository.save(gerente);
    }
}