package com.senai.conta_bancaria.domain.service;

import com.senai.conta_bancaria.domain.entity.Pagamento;
import com.senai.conta_bancaria.domain.entity.Taxa;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PagamentoDomainService {
    public BigDecimal calcularValorFinal(Pagamento pagamento){
        BigDecimal base = pagamento.getValorPago();
        BigDecimal total = base;
        if (pagamento.getTaxas() != null) {
            for (Taxa t : pagamento.getTaxas()) {
                BigDecimal perc = t.getPercentual() == null ? BigDecimal.ZERO : t.getPercentual();
                BigDecimal fixo = t.getValorFixo() == null ? BigDecimal.ZERO : t.getValorFixo();
                BigDecimal valorPerc = base.multiply(perc).divide(BigDecimal.valueOf(100));
                total = total.add(valorPerc).add(fixo);
            }
        }
        return total;
    }
}