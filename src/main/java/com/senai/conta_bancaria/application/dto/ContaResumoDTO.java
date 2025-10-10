package com.senai.conta_bancaria.application.dto;

import com.senai.conta_bancaria.domain.entity.Cliente;
import com.senai.conta_bancaria.domain.entity.Conta;
import com.senai.conta_bancaria.domain.entity.ContaCorrente;
import com.senai.conta_bancaria.domain.entity.ContaPoupanca;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

public record ContaResumoDTO(
        @NotBlank(message = "Número da conta não pode ser vazio")
        @Size(min = 6, max = 20, message = "Número da conta deve ter entre 6 e 20 caracteres")
        String numero,

        @NotBlank(message = "Tipo de conta não pode ser vazio")
        @Pattern(regexp = "^(CORRENTE|POUPANCA)$", message = "Tipo de conta deve ser 'CORRENTE' ou 'POUPANCA'")
        String tipo,

        @DecimalMin(value = "0.00", inclusive = true, message = "Saldo não pode ser negativo")
        @Digits(integer = 15, fraction = 2, message = "Saldo deve ter até 15 dígitos inteiros e 2 decimais")
        BigDecimal saldo
) {
    public Conta toEntity(Cliente cliente){
        if("CORRENTE".equalsIgnoreCase(tipo)){
            return ContaCorrente.builder()
                    .numero(this.numero)
                    .saldo(this.saldo)
                    .ativa(true)
                    .cliente(cliente)
                    .limite(new BigDecimal("500.0"))
                    .taxa(new BigDecimal("0.05"))
                    .build();
        } else if ("POUPANCA".equalsIgnoreCase(tipo)){
            return ContaPoupanca.builder()
                    .numero(this.numero)
                    .saldo(this.saldo)
                    .ativa(true)
                    .rendimento(new BigDecimal("0.01"))
                    .cliente(cliente)
                    .build();
        }
        return null;
    }

    public static ContaResumoDTO fromEntity(Conta c) {
        return new ContaResumoDTO(
                c.getNumero(),
                c.getTipo(),
                c.getSaldo()
        );
    }
}