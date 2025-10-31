package com.senai.conta_bancaria.domain.repository;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.CodigoAutenticacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodigoAutenticacaoRepository extends JpaRepository<CodigoAutenticacao, Long> {
    Optional<CodigoAutenticacao> findTopByClienteOrderByIdDesc(Cliente cliente);
}