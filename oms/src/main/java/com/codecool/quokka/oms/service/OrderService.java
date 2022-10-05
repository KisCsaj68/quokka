package com.codecool.quokka.oms.service;

import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.oms.MQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderService {

    private RabbitTemplate template;

    @Autowired
    public OrderService(RabbitTemplate template) {
        this.template = template;
    }

    public ResponseEntity createOrder(Orders data) {
        data.setStatus(OrderStatus.OPEN);
        System.out.println(data);

        // Send open order to persister via RMQ
        template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, data);
        System.out.println("Order received");


        // Ask the actual price from assetcache port 8000.

        // Fill the price to the order and update the order in DB.
        // Create position, send to persister RMQ
        // Store both Entity in-memory
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
