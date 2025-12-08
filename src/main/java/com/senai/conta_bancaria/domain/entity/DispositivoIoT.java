package com.senai.conta_bancaria.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DispositivoIoT {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String codigoSerial;

    private String chavePublica;

    private boolean ativo = true;

    @OneToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}