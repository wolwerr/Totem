package com.totem.pagamento.infrastructure.config;

import com.totem.pagamento.infrastructure.messaging.consumer.PagamentoKafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class KafkaConfig {

    @Bean
    public BlockingQueue<Map.Entry<Long, Double>> pedidoDataQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public PagamentoKafkaConsumer pedidoKafkaConsumer() {
        String servers = "localhost:9092";
        String topic = "pedido-pagamento";
        BlockingQueue<Map.Entry<Long, Double>> queue = pedidoDataQueue();
        return new PagamentoKafkaConsumer(servers, topic, queue);
    }
}

