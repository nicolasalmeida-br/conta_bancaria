package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.ContaAtualizacaoDTO;
import com.senai.conta_bancaria.application.dto.ContaResumoDTO;
import com.senai.conta_bancaria.application.dto.TransferenciaDTO;
import com.senai.conta_bancaria.application.dto.ValorSaqueDepositoDTO;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.ContaCorrente;
import com.senai.conta_bancaria.domain.entity.ContaPoupanca;
import com.senai.conta_bancaria.domain.exceptions.EntidadeNaoEncontradaException;
import com.senai.conta_bancaria.domain.exceptions.RendimentoInvalidoException;
import com.senai.conta_bancaria.domain.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pelo gerenciamento de contas bancárias.
 * Inclui operações de listagem, atualização, saque, depósito, transferência e aplicação de rendimento.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ContaService {

    private final ContaRepository repository;

    /**
     * ✅ NOVO: Busca uma conta ATIVA pelo seu ID (UUID).
     * Usado por fluxos internos (ex.: pagamentos/IoT).
     */
    @Transactional(readOnly = true)
    public Conta buscarPorId(String id) {
        return repository.findByIdAndAtivaTrue(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta"));
    }

    /**
     * Lista todas as contas ativas no sistema.
     *
     * @return Lista de DTOs resumidos das contas ativas
     */
    @Transactional(readOnly = true)
    public List<ContaResumoDTO> listarTodasContas() {
        return repository.findAllByAtivaTrue().stream()
                .map(ContaResumoDTO::fromEntity)
                .toList();
    }

    /**
     * Busca uma conta ativa pelo seu número.
     *
     * @param numero Número da conta
     * @return DTO resumido da conta
     * @throws EntidadeNaoEncontradaException se a conta não for encontrada
     */
    @Transactional(readOnly = true)
    public ContaResumoDTO buscarContaPorNumero(String numero) {
        return ContaResumoDTO.fromEntity(
                repository.findByNumeroAndAtivaTrue(numero)
                        .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta"))
        );
    }

    /**
     * Atualiza os dados de uma conta existente.
     *
     * @param numeroConta Número da conta a ser atualizada
     * @param dto DTO contendo os novos valores da conta
     * @return DTO resumido da conta atualizada
     */
    public ContaResumoDTO atualizarConta(String numeroConta, ContaAtualizacaoDTO dto) {
        var conta = buscaContaAtivaPorNumero(numeroConta);

        // Atualiza campos específicos de acordo com o tipo de conta
        if (conta instanceof ContaPoupanca poupanca) {
            poupanca.setRendimento(dto.rendimento());
        } else if (conta instanceof ContaCorrente corrente) {
            corrente.setLimite(dto.limite());
            corrente.setTaxa(dto.taxa());
        }

        conta.setSaldo(dto.saldo());
        return ContaResumoDTO.fromEntity(repository.save(conta));
    }

    /**
     * Desativa uma conta existente.
     *
     * @param numeroDaConta Número da conta a ser desativada
     */
    public void deletarConta(String numeroDaConta) {
        var conta = buscaContaAtivaPorNumero(numeroDaConta);
        conta.setAtiva(false);
        repository.save(conta);
    }

    /**
     * Busca uma conta ativa pelo número (uso interno).
     */
    private Conta buscaContaAtivaPorNumero(String numeroDaConta) {
        return repository.findByNumeroAndAtivaTrue(numeroDaConta)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conta"));
    }

    /**
     * Realiza um saque em uma conta.
     */
    public ContaResumoDTO sacar(String numeroConta, ValorSaqueDepositoDTO dto) {
        var conta = buscaContaAtivaPorNumero(numeroConta);
        conta.sacar(dto.valor());
        return ContaResumoDTO.fromEntity(repository.save(conta));
    }

    /**
     * Realiza um depósito em uma conta.
     */
    public ContaResumoDTO depositar(String numeroConta, ValorSaqueDepositoDTO dto) {
        var conta = buscaContaAtivaPorNumero(numeroConta);
        conta.depositar(dto.valor());
        return ContaResumoDTO.fromEntity(repository.save(conta));
    }

    /**
     * Realiza uma transferência entre duas contas ativas.
     */
    public ContaResumoDTO transferir(String numeroConta, TransferenciaDTO dto) {
        var contaOrigem = buscaContaAtivaPorNumero(numeroConta);
        var contaDestino = buscaContaAtivaPorNumero(dto.contaDestino());

        contaOrigem.transferir(dto.valor(), contaDestino);

        repository.save(contaDestino);
        return ContaResumoDTO.fromEntity(repository.save(contaOrigem));
    }

    /**
     * Aplica rendimento em uma conta poupança.
     */
    public ContaResumoDTO aplicarRendimento(String numeroDaConta) {
        var conta = buscaContaAtivaPorNumero(numeroDaConta);

        if (conta instanceof ContaPoupanca poupanca) {
            poupanca.aplicarRendimento();
            return ContaResumoDTO.fromEntity(repository.save(conta));
        }

        throw new RendimentoInvalidoException();
    }
}