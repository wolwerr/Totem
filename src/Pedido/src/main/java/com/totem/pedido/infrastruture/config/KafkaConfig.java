package com.totem.pedido.infrastruture.config;

import com.totem.pedido.infrastruture.messaging.consumer.PedidoKafkaConsumer;
import com.totem.pedido.infrastruture.messaging.producer.PedidoKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.topic}")
    private String topic;

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

    @Bean
    public PedidoKafkaProducer pedidoKafkaProducer() {
        return new PedidoKafkaProducer(bootstrapServers, topic);
    }
}

