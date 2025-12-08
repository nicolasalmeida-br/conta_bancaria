package com.senai.conta_bancaria.infrastructure.mqtt;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MqttService {

    private final MqttClient mqttClient;

    public void publicar(String topico, String mensagem) {
        try {
            MqttMessage msg = new MqttMessage(mensagem.getBytes());
            msg.setQos(1);
            mqttClient.publish(topico, msg);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao publicar mensagem MQTT", e);
        }
    }
}