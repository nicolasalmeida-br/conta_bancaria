package com.senai.conta_bancaria.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@DiscriminatorValue("Corrente")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class ContaCorrente extends Conta {

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal limite;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal taxa;
}