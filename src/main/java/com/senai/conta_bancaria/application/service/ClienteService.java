package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.ClienteRegistroDTO;
import com.senai.conta_bancaria.application.dto.ClienteResponseDTO;
import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.exceptions.ContaMesmoTipoException;
import com.senai.conta_bancaria.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.conta_bancaria.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável por gerenciar operações relacionadas a clientes.
 * Inclui cadastro, atualização, listagem e desativação de clientes e suas contas.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registra um novo cliente ou anexa uma conta a um cliente já existente.
     * Se o cliente já possui uma conta do mesmo tipo, lança {@link ContaMesmoTipoException}.
     *
     * @param dto DTO contendo informações do cliente e dados da conta.
     * @return DTO de resposta com os dados do cliente atualizado.
     */
    public ClienteResponseDTO registarClienteOuAnexarConta(ClienteRegistroDTO dto) {
        // Busca cliente ativo pelo CPF ou cria um novo cliente
        var cliente = repository.findByCpfAndAtivoTrue(dto.cpf())
                .orElseGet(() -> repository.save(dto.toEntity()));

        // Converte a conta do DTO em entidade vinculada ao cliente
        var contas = cliente.getContas();
        var novaConta = dto.contaDTO().toEntity(cliente);

        // Verifica se já existe conta ativa do mesmo tipo
        boolean jaTemTipo = contas.stream()
                .anyMatch(c -> c.getClass().equals(novaConta.getClass()) && c.isAtiva());
        if (jaTemTipo) throw new ContaMesmoTipoException();

        // Adiciona a nova conta
        cliente.getContas().add(novaConta);

        // Atualiza senha com hash
        cliente.setSenha(passwordEncoder.encode(dto.senha()));

        // Salva e retorna cliente atualizado
        return ClienteResponseDTO.fromEntity(repository.save(cliente));
    }

    /**
     * Lista todos os clientes ativos no sistema.
     *
     * @return Lista de DTOs com informações resumidas dos clientes ativos.
     */
    public List<ClienteResponseDTO> listarClientesAtivos() {
        return repository.findAllByAtivoTrue().stream()
                .map(ClienteResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Busca um cliente ativo pelo CPF.
     *
     * @param cpf CPF do cliente
     * @return DTO do cliente encontrado
     * @throws EntidadeNaoEncontradaException se o cliente não for encontrado
     */
    public ClienteResponseDTO buscarClienteAtivoPorCpf(String cpf) {
        var cliente = buscarPorCpfClienteAtivo(cpf);
        return ClienteResponseDTO.fromEntity(cliente);
    }

    /**
     * Atualiza informações básicas de um cliente ativo.
     *
     * @param cpf CPF do cliente a ser atualizado
     * @param dto DTO contendo os novos dados do cliente
     * @return DTO do cliente atualizado
     */
    public ClienteResponseDTO atualizarCliente(String cpf, ClienteRegistroDTO dto) {
        var cliente = buscarPorCpfClienteAtivo(cpf);

        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());

        return ClienteResponseDTO.fromEntity(repository.save(cliente));
    }

    /**
     * Desativa um cliente e todas as suas contas.
     *
     * @param cpf CPF do cliente a ser desativado
     */
    public void deletarCliente(String cpf) {
        var cliente = buscarPorCpfClienteAtivo(cpf);

        cliente.setAtivo(false);
        cliente.getContas().forEach(conta -> conta.setAtiva(false));
        repository.save(cliente);
    }

    /**
     * Busca um cliente ativo pelo CPF.
     *
     * @param cpf CPF do cliente
     * @return Cliente encontrado
     * @throws EntidadeNaoEncontradaException se o cliente não estiver ativo ou não existir
     */
    private Cliente buscarPorCpfClienteAtivo(String cpf) {
        return repository.findByCpfAndAtivoTrue(cpf)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cliente"));
    }
}