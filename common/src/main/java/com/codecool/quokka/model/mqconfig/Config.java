package com.codecool.quokka.model.mqconfig;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

public class Config {

    public static final String ORDER_QUEUE = "order_queue";
    public static final String LIMIT_ORDER_QUEUE = "limit_order_queue";
    public static final String FILLED_ORDER_QUEUE = "filled_order_queue";
    public static final String POSITION_QUEUE = "position_queue";
    public static final String EXCHANGE = "oms_exchange";
    public static final String ORDER_ROUTING_KEY = "order_routing_key";
    public static final String LIMIT_ORDER_ROUTING_KEY = "limit_order_routing_key";
    public static final String FILLED_ORDER_ROUTING_KEY = "filled_order_routing_key";
    public static final String POSITION_ROUTING_KEY = "position_routing_key";

    public static TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    public static Declarables topicBindings(TopicExchange topicExchange) {
        Queue orderQueue = new Queue(ORDER_QUEUE);
        Queue limitOrderQueue = new Queue(LIMIT_ORDER_QUEUE);
        Queue positionQueue = new Queue(POSITION_QUEUE);
        Queue filledOrderQueue = new Queue(FILLED_ORDER_QUEUE);
        return new Declarables(
                orderQueue,
                positionQueue,
                limitOrderQueue,
                filledOrderQueue,
                topicExchange,
                BindingBuilder
                        .bind(orderQueue)
                        .to(topicExchange).with(ORDER_ROUTING_KEY),
                BindingBuilder
                        .bind(limitOrderQueue)
                        .to(topicExchange).with(LIMIT_ORDER_ROUTING_KEY),
                BindingBuilder
                        .bind(filledOrderQueue)
                        .to(topicExchange).with(FILLED_ORDER_ROUTING_KEY),
                BindingBuilder
                        .bind(positionQueue)
                        .to(topicExchange).with(POSITION_ROUTING_KEY));
    }

//    public static MessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }

//    public static AmqpTemplate template(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setMessageConverter(messageConverter());
//        return template;
//    }
}
