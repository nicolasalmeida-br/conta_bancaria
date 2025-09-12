package com.senai.conta_bancaria.domain.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class ContaPoupanca extends Conta {

    private double rendimento;
}