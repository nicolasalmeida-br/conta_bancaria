package com.senai.conta_bancaria.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "gerentes")
public class Gerente extends Usuario {

}