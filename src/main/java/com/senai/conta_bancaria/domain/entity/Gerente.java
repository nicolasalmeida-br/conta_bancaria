package com.senai.conta_bancaria.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "gerentes")
public class Gerente extends Usuario {

    @Column(name = "codigo_funcionario", nullable = false, unique = true)
    private String codigoFuncionario;

    @OneToMany(mappedBy = "gerente", fetch = FetchType.LAZY)
    private List<Cliente> clientes;
}