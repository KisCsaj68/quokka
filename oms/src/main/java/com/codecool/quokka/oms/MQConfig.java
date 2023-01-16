package com.codecool.quokka.oms;

import com.codecool.quokka.model.mqconfig.Config;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
@EnableRabbit
public class MQConfig {

    @Bean
    public TopicExchange exchange() {
        return Config.exchange();
    }

    @Bean
    public Declarables topicBindings() {
        return Config.topicBindings(exchange());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    @Bean // TODO: remove
//    public AmqpTemplate template(ConnectionFactory connectionFactory) {
//        return Config.template(connectionFactory);
//    }

//    @Bean
//    public RabbitListenerEndpointRegistry registry() {
//        return new RabbitListenerEndpointRegistry();
//    }
}