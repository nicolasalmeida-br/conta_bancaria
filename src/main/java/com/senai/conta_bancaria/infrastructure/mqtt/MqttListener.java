package com.senai.conta_bancaria.infrastructure.mqtt;

import com.senai.conta_bancaria.application.service.PagamentoAppService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

/**
 * Listener MQTT que recebe validações do dispositivo IoT.
 *
 * Fluxo:
 * 1) Backend publica em: banco/autenticacao/{clienteId} → "CODE:XXXXXX"
 * 2) Dispositivo IoT responde em: banco/validacao/{clienteId} → "CODE:XXXXXX"
 * 3) Este listener recebe a validação e chama:
 *        pagamentoAppService.validarCodigo(clienteId, codigo)
 */
@Component
public class MqttListener {

    private final MqttClient client;
    private final PagamentoAppService pagamentoAppService;

    public MqttListener(MqttClient client, PagamentoAppService pagamentoAppService) {
        this.client = client;
        this.pagamentoAppService = pagamentoAppService;
    }

    @PostConstruct
    public void init() throws MqttException {

        if (!client.isConnected()) {
            client.connect();
        }

        // Assina: banco/validacao/{clienteId}
        client.subscribe("banco/validacao/+", (topic, message) -> {
            try {
                System.out.println("[MQTT] Mensagem recebida no tópico: " + topic);

                String[] parts = topic.split("/");

                // Tópico esperado → banco / validacao / {clienteId}
                if (parts.length != 3) {
                    System.err.println("[MQTT] Tópico fora do formato esperado.");
                    return;
                }

                String clienteId = parts[2];
                String payload = new String(message.getPayload()).trim();

                System.out.println("[MQTT] Payload recebido: " + payload);

                if (!payload.startsWith("CODE:")) {
                    System.err.println("[MQTT] Payload inválido. Esperado: CODE:XXXXXX");
                    return;
                }

                String codigo = payload.substring(5);

                // Chama o fluxo de validação no backend
                pagamentoAppService.validarCodigo(clienteId, codigo);

                System.out.println("[MQTT] Código validado com sucesso para cliente: " + clienteId);

            } catch (Exception e) {
                System.err.println("[MQTT] Erro ao processar mensagem:");
                e.printStackTrace();
            }
        });

        System.out.println("[MQTT] Listener ativo em: banco/validacao/+");
    }
}