package com.senai.conta_bancaria.infrastructure.mqtt;

import com.senai.conta_bancaria.application.service.PagamentoAppService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

/**
 * Listener MQTT que recebe a validação do dispositivo IoT.
 *
 * Fluxo:
 * 1) Backend publica em banco/autenticacao/{clienteId} → "CODE:XXXXXX"
 * 2) Dispositivo IoT recebe, autentica digital e publica em banco/validacao/{clienteId} → "CODE:XXXXXX"
 * 3) Este listener recebe e chama PagamentoAppService.validarCodigo(clienteId, codigo)
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

        // Assina todos os tópicos: banco/validacao/{clienteId}
        client.subscribe("banco/validacao/+", (topic, message) -> {
            try {
                // Exemplo de tópico: banco/validacao/1
                String[] parts = topic.split("/");
                if (parts.length < 3) {
                    return; // formato inesperado
                }

                String clienteId = parts[2];
                String payload = new String(message.getPayload());

                if (payload.startsWith("CODE:")) {
                    String codigo = payload.substring(5);
                    pagamentoAppService.validarCodigo(clienteId, codigo);
                    System.out.println("Código IoT validado para cliente " + clienteId);
                }

            } catch (Exception e) {
                e.printStackTrace(); // em produção, usar logger
            }
        });

        System.out.println("MqttListener assinando tópicos: banco/validacao/+");
    }
}