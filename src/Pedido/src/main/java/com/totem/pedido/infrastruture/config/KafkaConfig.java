package com.totem.pedido.infrastruture.config;

import com.totem.pedido.infrastruture.messaging.consumer.PedidoKafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class KafkaConfig {

    @Bean
    public BlockingQueue<Map.Entry<Long, String>> clienteDataQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public PedidoKafkaConsumer pedidoKafkaConsumer() {
        String servers = "localhost:9092";
        String topic = "totem";
        BlockingQueue<Map.Entry<Long, String>> queue = clienteDataQueue();
        return new PedidoKafkaConsumer(servers, topic, queue);
    }
}

