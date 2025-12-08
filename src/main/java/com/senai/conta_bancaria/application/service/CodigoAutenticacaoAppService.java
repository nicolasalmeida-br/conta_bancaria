package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.domain.entity.CodigoAutenticacao;
import com.senai.conta_bancaria.domain.exceptions.AutenticacaoIoTExpiradaException;
import com.senai.conta_bancaria.domain.repository.CodigoAutenticacaoRepository;
import com.senai.conta_bancaria.domain.repository.DispositivoIoTRepository;
import com.senai.conta_bancaria.infrastructure.mqtt.MqttGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CodigoAutenticacaoAppService {

    private final CodigoAutenticacaoRepository codigoAutenticacaoRepository;
    private final DispositivoIoTRepository dispositivoIoTRepository;
    private final MqttGateway mqttGateway;

    private final Random random = new Random();

    /**
     * Inicia o fluxo de autenticação IoT:
     * - gera código
     * - salva no banco
     * - envia via MQTT para banco/autenticacao/{clienteId}
     */
    @Transactional
    public void iniciarAutenticacao(String clienteId) {

        var dispositivo = dispositivoIoTRepository.findByCliente_IdAndAtivoTrue(clienteId)
                .orElseThrow(() -> new AutenticacaoIoTExpiradaException());

        String codigo = gerarCodigo();

        CodigoAutenticacao ca = new CodigoAutenticacao();
        ca.setId(java.util.UUID.randomUUID().toString());
        ca.setCodigo(codigo);
        ca.setExpiraEm(LocalDateTime.now().plusMinutes(2)); // tempo válido
        ca.setValidado(false);
        ca.setCliente(dispositivo.getCliente());

        codigoAutenticacaoRepository.save(ca);

        // Envia para o dispositivo IoT
        mqttGateway.enviarCodigoAutenticacao(clienteId, codigo);
    }

    /**
     * Valida o código recebido via MQTT (listener).
     */
    @Transactional
    public void validarCodigo(String clienteId, String codigoRecebido) {

        var codigo = codigoAutenticacaoRepository
                .findTopByCliente_IdOrderByExpiraEmDesc(clienteId)
                .orElseThrow(() -> new RuntimeException("Nenhum código encontrado"));

        boolean expirado = codigo.getExpiraEm().isBefore(LocalDateTime.now());
        boolean diferente = !codigo.getCodigo().equals(codigoRecebido);

        if (codigo.isValidado() || expirado || diferente) {
            throw new AutenticacaoIoTExpiradaException();
        }

        codigo.setValidado(true);
        codigoAutenticacaoRepository.save(codigo);
    }

    private String gerarCodigo() {
        int num = 100000 + random.nextInt(900000);
        return String.valueOf(num);
    }
}