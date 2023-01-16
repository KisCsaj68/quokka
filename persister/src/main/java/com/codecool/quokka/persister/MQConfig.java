package com.codecool.quokka.persister;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.codecool.quokka.model.mqconfig.Config;

@Configuration
public class MQConfig {

    @Bean
    public TopicExchange exchange() {
        return Config.exchange();
    }

    @Bean
    public Declarables topicBindings(TopicExchange topicExchange) {
        return Config.topicBindings(exchange());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    @Bean
//    public AmqpTemplate template(ConnectionFactory connectionFactory) {
//        return Config.template(connectionFactory);
//    }
}