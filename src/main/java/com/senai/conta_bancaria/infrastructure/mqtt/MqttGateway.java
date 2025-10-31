package com.senai.conta_bancaria.infrastructure.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttGateway {
    private final MqttClient client;
    public MqttGateway(MqttClient client) { this.client = client; }

    public void publish(String topic, byte[] payload){
        try {
            client.publish(topic, new MqttMessage(payload));
        } catch (Exception e){
            throw new RuntimeException("Falha ao publicar MQTT", e);
        }
    }
}