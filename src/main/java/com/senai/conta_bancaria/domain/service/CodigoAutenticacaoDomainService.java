package com.senai.conta_bancaria.domain.service;

import com.senai.conta_bancaria.domain.entity.CodigoAutenticacao;
import com.senai.conta_bancaria.domain.exceptions.AutenticacaoIoTExpiradaException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CodigoAutenticacaoDomainService {

    public void validarCodigo(CodigoAutenticacao registro, String recebido) {

        if (registro.isValidado()) {
            throw new AutenticacaoIoTExpiradaException("Código já utilizado.");
        }

        if (!registro.getCodigo().equals(recebido)) {
            throw new AutenticacaoIoTExpiradaException("Código inválido.");
        }

        if (registro.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new AutenticacaoIoTExpiradaException("Código expirado.");
        }

        registro.setValidado(true);
    }
}