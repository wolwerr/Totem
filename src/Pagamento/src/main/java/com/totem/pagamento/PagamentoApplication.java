package com.totem.pagamento;

import com.totem.pagamento.infrastructure.messaging.consumer.PagamentoKafkaConsumer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PagamentoApplication {

    private final PagamentoKafkaConsumer consumer;

    @Autowired
    public PagamentoApplication(PagamentoKafkaConsumer consumer) {
        this.consumer = consumer;
    }

    public static void main(String[] args) {
        SpringApplication.run(PagamentoApplication.class, args);
    }

    @PostConstruct
    public void startConsumer() {
        Thread consumerThread = new Thread(consumer::runConsumer);
        consumerThread.start();
    }
}
