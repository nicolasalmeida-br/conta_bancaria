package com.senai.conta_bancaria.infrastructure.mqtt;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqttGateway {

    private final MqttClient client;

    public void publicar(String topic, String payload) {
        try {
            MqttMessage msg = new MqttMessage(payload.getBytes());
            msg.setQos(1);
            client.publish(topic, msg);

            System.out.println("MQTT -> " + topic + " | " + payload);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao publicar no MQTT", e);
        }
    }

    public void enviarCodigoAutenticacao(String clienteId, String codigo) {
        publicar("banco/autenticacao/" + clienteId, "CODE:" + codigo);
    }
}