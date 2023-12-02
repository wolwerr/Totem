package com.totem.pedido;

import com.totem.pedido.infrastruture.messaging.consumer.PedidoKafkaConsumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class PedidoApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(PedidoApplication.class, args);

        // Cria uma fila de bloqueio para armazenar os dados de cliente recebidos do Kafka
        BlockingQueue<String> clienteDataQueue = new LinkedBlockingQueue<>();

        // Cria uma instância do consumidor Kafka
        PedidoKafkaConsumer consumer = new PedidoKafkaConsumer("localhost:9092", "clientePedidoTopic", clienteDataQueue);

        // Inicia o consumidor em uma nova thread
        Thread consumerThread = new Thread(consumer::runConsumer);
        consumerThread.start();
    }
}

