package com.senai.conta_bancaria.infrastructure.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Bean
    public MqttClient mqttClient() throws Exception {
        String broker = System.getProperty("mqtt.broker", "tcp://localhost:1883");
        String clientId = "banco-backend-" + System.currentTimeMillis();
        MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
        client.connect();
        return client;
    }

    @Bean
    public MqttGateway mqttGateway(MqttClient client){
        return new MqttGateway(client);
    }
}
