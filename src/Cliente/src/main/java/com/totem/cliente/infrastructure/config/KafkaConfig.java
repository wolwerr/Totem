package com.totem.cliente.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    @Bean
    public ClienteKafkaProducer clienteKafkaProducer() {
        return new ClienteKafkaProducer(bootstrapServers, topic);
    }
}
