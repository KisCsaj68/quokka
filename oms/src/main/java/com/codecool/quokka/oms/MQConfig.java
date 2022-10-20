package com.codecool.quokka.oms;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    public static final String ORDER_QUEUE = "order_queue";
    public static final String POSITION_QUEUE = "position_queue";
    public static final String EXCHANGE = "order_exchange";
    public static final String ORDER_ROUTING_KEY = "order_routingKey";
    public static final String POSITION_ROUTING_KEY = "position_routingKey";

//    @Bean
//    public Queue queue() {
//        return  new Queue(QUEUE);
//    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

//    @Bean
//    public Binding binding(Queue queue, TopicExchange exchange) {
//        return BindingBuilder
//                .bind(queue)
//                .to(exchange)
//                .with(ROUTING_KEY);
//    }

    @Bean
    public Declarables topicBindings(TopicExchange topicExchange) {
        Queue orderQueue = new Queue(ORDER_QUEUE);
        Queue positionQueue = new Queue(POSITION_QUEUE);

        return new Declarables(
                orderQueue,
                positionQueue,
                topicExchange,
                BindingBuilder
                        .bind(orderQueue)
                        .to(topicExchange).with(ORDER_ROUTING_KEY),
                BindingBuilder
                        .bind(positionQueue)
                        .to(topicExchange).with(POSITION_ROUTING_KEY));
    }

    @Bean
    public MessageConverter messageConverter() {
        return  new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return  template;
    }
}
