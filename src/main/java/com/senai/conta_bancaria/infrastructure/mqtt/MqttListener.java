package com.senai.conta_bancaria.infrastructure.mqtt;

import com.senai.conta_bancaria.application.service.AutenticacaoIoTAppService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqttListener {

    private final MqttClient client;
    private final AutenticacaoIoTAppService authService;

    @PostConstruct
    public void init() {

        try {
            client.subscribe("banco/validacao/+", (topic, message) -> {

                String payload = new String(message.getPayload());
                System.out.println("MQTT Recebido -> " + topic + " | " + payload);

                String clienteId = topic.substring(topic.lastIndexOf("/") + 1);

                if (!payload.startsWith("CODE:")) return;

                String codigo = payload.substring(5);

                authService.validarCodigo(clienteId, codigo);
            });

            System.out.println("MQTT Listener ativo!");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao iniciar listener MQTT", e);
        }
    }
}