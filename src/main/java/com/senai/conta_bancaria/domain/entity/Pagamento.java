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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false, length = 36)
    private String id;

    @ManyToOne(optional = false)
    private Conta conta;

    @Column(nullable = false)
    private String boleto;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorPago;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorFinal;

    @Column(nullable = false)
    private LocalDateTime dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;

    @ManyToMany
    @JoinTable(
            name = "pagamento_taxa",
            joinColumns = @JoinColumn(name = "pagamento_id"),
            inverseJoinColumns = @JoinColumn(name = "taxa_id")
    )
    private Set<Taxa> taxas = new HashSet<>();
}