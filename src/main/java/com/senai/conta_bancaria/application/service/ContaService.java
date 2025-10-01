package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.ContaAtualizacaoDTO;
import com.senai.conta_bancaria.application.dto.ContaResumoDTO;
import com.senai.conta_bancaria.application.dto.TransferenciaDTO;
import com.senai.conta_bancaria.application.dto.ValorSaqueDepositoDTO;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.ContaCorrente;
import com.senai.conta_bancaria.domain.entity.ContaPoupanca;
import com.senai.conta_bancaria.domain.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContaService {
    private final ContaRepository repository;

    @Transactional(readOnly = true)
    public List<ContaResumoDTO> listarContasAtivas() {
        return repository.findAllByAtivaTrue().stream()
                .map(ContaResumoDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContaResumoDTO buscarContaPorNumero(String numero) {
        return ContaResumoDTO.fromEntity(
                repository.findByNumeroAndAtivaTrue(numero)
                        .orElseThrow(() -> new RuntimeException("Conta não encontrada"))
        );
    }

        public ContaResumoDTO atualizarConta(String numero, ContaAtualizacaoDTO dto) {
            var conta = buscarContaAtivaPorNumero(numero);

        if (conta instanceof ContaPoupanca poupanca) {
            poupanca.setRendimento(dto.rendimento());
        } else if (conta instanceof ContaCorrente corrente) {
            corrente.setLimite(dto.limite());
            corrente.setTaxa(dto.taxa());
        }

        conta.setSaldo(dto.saldo());

        return ContaResumoDTO.fromEntity(repository.save(conta));
    }
    public void deletarConta(String numero) {
        var conta = buscarContaAtivaPorNumero(numero);

        conta.setAtiva(false);
        repository.save(conta);
    }

    private Conta buscarContaAtivaPorNumero(String numero) {
        return repository.findByNumeroAndAtivaTrue(numero)
            .orElseThrow(() -> new RuntimeException("Conta não encontrada.")
        );
    }

    public ContaResumoDTO sacar(String numero, ValorSaqueDepositoDTO dto) {
        var conta = buscarContaAtivaPorNumero(numero);
        conta.sacar(dto.valor());

        return ContaResumoDTO.fromEntity(repository.save(conta));
    }

    public ContaResumoDTO depositar(String numero, ValorSaqueDepositoDTO dto) {
        var conta = buscarContaAtivaPorNumero(numero);
        conta.depositar(dto.valor());

        return ContaResumoDTO.fromEntity(repository.save(conta));
    }

    public ContaResumoDTO transferir(String numero, TransferenciaDTO dto) {
        var contaOrigem = buscarContaAtivaPorNumero(numero);
        var contaDestino = buscarContaAtivaPorNumero(dto.contaDestino());

        contaOrigem.transferir(dto.valor(), contaDestino);

        repository.save(contaDestino);
        return ContaResumoDTO.fromEntity(repository.save(contaOrigem));
    }
}