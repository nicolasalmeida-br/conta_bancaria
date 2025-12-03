package com.senai.conta_bancaria.domain.service;

import com.senai.conta_bancaria.domain.entity.Pagamento;
import com.senai.conta_bancaria.domain.entity.Taxa;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PagamentoDomainService {

    public BigDecimal calcularValorFinal(Pagamento pagamento) {
        BigDecimal base = pagamento.getValorPago();
        BigDecimal total = base;

        if (pagamento.getTaxas() != null) {
            for (Taxa t : pagamento.getTaxas()) {

                BigDecimal perc = t.getPercentual() != null ? t.getPercentual() : BigDecimal.ZERO;
                BigDecimal fixo = t.getValorFixo() != null ? t.getValorFixo() : BigDecimal.ZERO;

                BigDecimal valorPerc = base.multiply(perc)
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

                total = total.add(valorPerc).add(fixo);
            }
        }

        return total;
    }

    public void aplicarCalculoValorFinal(Pagamento pagamento) {
        pagamento.setValorFinal(calcularValorFinal(pagamento));
    }
}