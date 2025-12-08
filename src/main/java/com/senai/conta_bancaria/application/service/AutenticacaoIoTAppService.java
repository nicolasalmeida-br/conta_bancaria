package com.senai.conta_bancaria.application.service;

import com.senai.conta_bancaria.domain.entity.CodigoAutenticacao;
import com.senai.conta_bancaria.domain.exceptions.AutenticacaoIoTExpiradaException;
import com.senai.conta_bancaria.domain.repository.CodigoAutenticacaoRepository;
import com.senai.conta_bancaria.domain.repository.DispositivoIoTRepository;
import com.senai.conta_bancaria.domain.service.CodigoAutenticacaoDomainService;
import com.senai.conta_bancaria.infrastructure.mqtt.MqttGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AutenticacaoIoTAppService {

    private final DispositivoIoTRepository dispositivoRepo;
    private final CodigoAutenticacaoRepository codigoRepo;
    private final CodigoAutenticacaoDomainService domainService;
    private final MqttGateway mqttGateway;

    @Transactional
    public void iniciarAutenticacao(String clienteId) {

        var disp = dispositivoRepo.findByCliente_IdAndAtivoTrue(clienteId)
                .orElseThrow(() -> new AutenticacaoIoTExpiradaException("Dispositivo não encontrado ou inativo."));

        String codigo = gerarCodigo();
        LocalDateTime expira = LocalDateTime.now().plusSeconds(45);

        CodigoAutenticacao registro = new CodigoAutenticacao();
        registro.setCodigo(codigo);
        registro.setExpiraEm(expira);
        registro.setValidado(false);
        registro.setCliente(disp.getCliente());

        codigoRepo.save(registro);

        mqttGateway.enviarCodigoAutenticacao(clienteId, codigo);
    }

    @Transactional
    public void validarCodigo(String clienteId, String recebido) {

        var registro = codigoRepo.findTopByCliente_IdOrderByExpiraEmDesc(clienteId)
                .orElseThrow(() -> new AutenticacaoIoTExpiradaException("Nenhum código gerado para este cliente."));

        domainService.validarCodigo(registro, recebido);

        codigoRepo.save(registro);
    }

    private String gerarCodigo() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}