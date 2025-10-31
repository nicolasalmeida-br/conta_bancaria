package com.senai.conta_bancaria.infrastructure.mqtt;

import com.senai.conta_bancaria.application.service.PagamentoAppService;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.stereotype.Component;

/**
 * Listener simples que, ao receber uma mensagem no tópico banco/validacao/{clienteId},
 * chama o serviço para validar o código.
 */
@Component
public class MqttListener {

    private final MqttClient client;
    private final PagamentoAppService pagamentos;

    public MqttListener(MqttClient client, PagamentoAppService pagamentos) {
        this.client = client;
        this.pagamentos = pagamentos;
    }

    @PostConstruct
    public void init() throws Exception {
        client.subscribe("banco/validacao/+",
                (topic, message) -> {
                    try {
                        String[] parts = topic.split("/");
                        String clienteId = parts[2];
                        String body = new String(message.getPayload());

                        if (body.startsWith("CODE:")) {
                            String code = body.substring(5);
                            pagamentos.validarCodigo(clienteId, code);
                        }
                    } catch (Exception e) {
                    }
                }
        );
    }
}