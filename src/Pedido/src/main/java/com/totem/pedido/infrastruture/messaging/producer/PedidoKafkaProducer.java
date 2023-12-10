package com.totem.pedido.infrastruture.messaging.producer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.totem.pedido.domain.Pedido;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;


public class PedidoKafkaProducer {

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PedidoKafkaProducer(String servers, String topic) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.producer = new KafkaProducer<>(props);
        this.topic = topic;
    }

    public void enviarMensagemPedido(Pedido pedido) {
        try {
            // Extrair pedidoId e valorTotal do objeto Pedido
            Long pedidoId = pedido.getId();
            Double valorTotal = pedido.getValorTotal();

            // Criar um objeto JSON com pedidoId e valorTotal
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("pedidoId", pedidoId);
            jsonNode.put("valorTotal", valorTotal);

            // Serializar o JSON e enviar como valor da mensagem
            String json = jsonNode.toString();
            enviarMensagem(pedidoId.toString(), json);
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