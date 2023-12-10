package com.totem.pagamento.infrastructure.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;


public class PagamentoKafkaConsumer {

    private final KafkaConsumer<String, String> consumer;
    private final BlockingQueue<Map.Entry<Long, Double>> pagamentoDataQueue;

    public PagamentoKafkaConsumer(String servers, String topic, BlockingQueue<Map.Entry<Long, Double>> pagamentoDataQueue) {
        this.pagamentoDataQueue = pagamentoDataQueue;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "pedido-pagamento");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public void runConsumer() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Mensagem recebida - Tópico: %s, Chave: %s, Valor: %s%n", record.topic(), record.key(), record.value());
                    try {
                        // Parse the JSON message
                        JsonNode messageJson = objectMapper.readTree(record.value());
                        // Extract the pedidoId and valorTotal from the JSON
                        Long pedidoId = messageJson.get("pedidoId").asLong();
                        Double valorTotal = messageJson.get("valorTotal").asDouble();
                        // Create a Map.Entry and put it in the queue
                        Map.Entry<Long, Double> pedidoData = new AbstractMap.SimpleEntry<>(pedidoId, valorTotal);
                        pagamentoDataQueue.put(pedidoData);
                    } catch (JsonProcessingException | InterruptedException e) {
                        System.err.println("Erro ao processar a mensagem: " + e.getMessage());
                        if (e instanceof InterruptedException) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            }
        } finally {
            consumer.close();
            System.out.println("Consumidor Kafka fechado.");
        }
    }
}