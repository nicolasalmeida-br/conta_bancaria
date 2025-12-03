package com.senai.conta_bancaria.domain.repository;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.CodigoAutenticacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodigoAutenticacaoRepository extends JpaRepository<CodigoAutenticacao, String> {

    // pega o último código criado para o cliente, com base na data de expiração (mais recente)
    Optional<CodigoAutenticacao> findTopByClienteOrderByExpiraEmDesc(Cliente cliente);
}