package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.ContaResumoDTO;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.ContaCorrente;
import com.senai.conta_bancaria.domain.entity.ContaPoupanca;
import com.senai.conta_bancaria.domain.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ContaResumoDTO atualizarConta(String numero, ContaResumoDTO dto) {
        var conta = repository.findByNumeroAndAtivaTrue(numero)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada."));

        conta.setSaldo(dto.saldo());

        if (conta instanceof ContaCorrente corrente && dto.limite() != null && dto.taxa() != null) {
            corrente.setLimite(dto.limite());
            corrente.setTaxa(dto.taxa());
        } else if (conta instanceof ContaPoupanca poupanca && dto.rendimento() != null) {
            poupanca.setRendimento(dto.rendimento());
        }

        return ContaResumoDTO.fromEntity(repository.save(conta));
    }
    public void deletarConta(String numero) {
        var conta = repository.findByNumeroAndAtivaTrue(numero)
            .orElseThrow(() -> new RuntimeException("Conta não encontrada."));

        conta.setAtiva(false);
        repository.save(conta);
    }
}