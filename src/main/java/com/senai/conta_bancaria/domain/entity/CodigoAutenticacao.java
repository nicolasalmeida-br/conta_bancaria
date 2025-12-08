package com.senai.conta_bancaria.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class CodigoAutenticacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String codigo;

    private LocalDateTime expiraEm;

    private boolean validado = false;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}