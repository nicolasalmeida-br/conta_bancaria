package com.senai.conta_bancaria.infrastructure.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Value("${mqtt.broker:tcp://localhost:1883}")
    private String broker;

    @Value("${mqtt.clientId:conta-bancaria}")
    private String clientId;

    @Bean
    public MqttClient mqttClient() throws Exception {

        MqttClient client = new MqttClient(
                broker,
                clientId + "-" + System.currentTimeMillis(),
                new MemoryPersistence()
        );

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);

        client.connect(options);

        System.out.println("MQTT conectado ao broker: " + broker);

        return client;
    }
}