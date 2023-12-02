package com.totem.cliente.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.totem.cliente.domain.Cliente;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;


public class ClienteKafkaProducer {
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClienteKafkaProducer(String servers, String topic) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
        this.topic = topic;
    }

    public void enviarMensagemCliente(Cliente cliente) {
        try {
            // Extrair clienteId e clienteNome do objeto Cliente
            Long clienteId = cliente.getId();
            String clienteNome = cliente.getNome();

            // Criar um objeto JSON com clienteId e clienteNome
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("clienteId", clienteId);
            jsonNode.put("clienteNome", clienteNome);

            // Serializar o JSON e enviar como valor da mensagem
            String json = jsonNode.toString();
            enviarMensagem(clienteId.toString(), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviarMensagem(String key, String value) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
        System.out.println("Enviando mensagem - Chave: " + key + ", Valor: " + value);
        producer.send(record);
    }

    public void close() {
        producer.close();
    }
}
