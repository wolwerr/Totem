package com.totem.pedido.infrastruture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class AppConfig {

    @Bean
    public BlockingQueue<Long> clienteIdQueue() {

        return new LinkedBlockingQueue<>();
    }

    @Bean
    public BlockingQueue<Map.Entry<Long, String>> clienteDataQueue() {
        return new LinkedBlockingQueue<>();
    }
}

