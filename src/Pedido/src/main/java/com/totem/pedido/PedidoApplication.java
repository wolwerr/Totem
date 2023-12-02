package com.totem.pedido;

import com.totem.pedido.infrastruture.messaging.consumer.PedidoKafkaConsumer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class PedidoApplication {
    private final PedidoKafkaConsumer consumer;

    @Autowired
    public PedidoApplication(PedidoKafkaConsumer consumer) {
        this.consumer = consumer;
    }

    public static void main(String[] args) {
        SpringApplication.run(PedidoApplication.class, args);
    }

    @PostConstruct
    public void startConsumer() {
        Thread consumerThread = new Thread(consumer::runConsumer);
        consumerThread.start();
    }
}
