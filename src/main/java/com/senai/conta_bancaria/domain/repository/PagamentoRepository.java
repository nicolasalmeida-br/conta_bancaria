package com.senai.conta_bancaria.domain.repository;

import com.senai.conta_bancaria.domain.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, String> {
}