package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.application.dto.ContaCorrenteDTO;
import com.senai.conta_bancaria.application.dto.ContaPoupancaDTO;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.ContaCorrente;
import com.senai.conta_bancaria.domain.entity.ContaPoupanca;
import com.senai.conta_bancaria.domain.repository.ContaRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class ContaService {
    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public ContaCorrenteDTO salvarCorrente(ContaCorrenteDTO dto) {
        ContaCorrente conta = dto.toEntity();
        ContaCorrente salvo = contaRepository.save(conta) instanceof ContaCorrente cc ? cc : null;
        return ContaCorrenteDTO.fromEntity(salvo);
    }

    public ContaPoupancaDTO salvarPoupanca(ContaPoupancaDTO dto) {
        ContaPoupanca conta = dto.toEntity();
        ContaPoupanca salvo = contaRepository.save(conta) instanceof ContaPoupanca cp ? cp : null;
        return ContaPoupancaDTO.fromEntity(salvo);
    }

    public void depositar(String id, double valor) {
        Conta conta = contaRepository.findById(id).orElseThrow();
        BigDecimal valorBD = BigDecimal.valueOf(valor);
        conta.setSaldo(conta.getSaldo().add(valorBD));
        contaRepository.save(conta);
    }

    public void sacar(String id, double valor) {
        Conta conta = contaRepository.findById(id).orElseThrow();
        BigDecimal valorBD = BigDecimal.valueOf(valor);
        if (valor > 0 && conta.getSaldo().compareTo(valorBD) >= 0) {
            conta.setSaldo(conta.getSaldo().subtract(valorBD));
            contaRepository.save(conta);
        }
    }

    public ContaCorrenteDTO buscarCorrente(String id) {
        Conta conta = contaRepository.findById(id).orElseThrow();
        if (conta instanceof ContaCorrente cc) {
            return ContaCorrenteDTO.fromEntity(cc);
        }
        throw new RuntimeException("Conta não é do tipo corrente!");
    }

    public ContaPoupancaDTO buscarPoupanca(String id) {
        Conta conta = contaRepository.findById(id).orElseThrow();
        if (conta instanceof ContaPoupanca cp) {
            return ContaPoupancaDTO.fromEntity(cp);
        }
        throw new RuntimeException("Conta não é do tipo poupança!");
    }
}