package com.codecool.quokka.oms;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ConnectionFactory getConnectionFactory() {
        return new ConnectionFactory();
    }
}
