package com.senai.conta_bancaria.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class PagamentoResumoDTO {
    public Long id;
    public Long contaId;
    public String boleto;
    public BigDecimal valorPago;
    public BigDecimal valorFinal;
    public LocalDateTime dataPagamento;
    public String status;
    public Set<Long> taxasIds;

    public PagamentoResumoDTO() {}

    public PagamentoResumoDTO(Long id, Long contaId, String boleto, BigDecimal valorPago,
                              BigDecimal valorFinal, LocalDateTime dataPagamento,
                              String status, Set<Long> taxasIds) {
        this.id = id;
        this.contaId = contaId;
        this.boleto = boleto;
        this.valorPago = valorPago;
        this.valorFinal = valorFinal;
        this.dataPagamento = dataPagamento;
        this.status = status;
        this.taxasIds = taxasIds;
    }
}
