package com.senai.conta_bancaria.domain.entity;

import com.senai.conta_bancaria.domain.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Conta conta;

    private String boleto;

    private BigDecimal valorPago;

    private LocalDateTime dataPagamento;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    @ManyToMany
    private Set<Taxa> taxas = new HashSet<>();

}