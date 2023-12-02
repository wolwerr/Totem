package com.totem.cliente.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public ClienteKafkaProducer clienteKafkaProducer() {
        // Substitua com as configurações apropriadas
        return new ClienteKafkaProducer("localhost:9092", "clientePedidoTopic");
    }
}
