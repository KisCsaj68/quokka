package com.codecool.quokka.oms.service;

import com.codecool.quokka.model.assets.Asset;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.model.order.OrderStatus;
import com.codecool.quokka.oms.MQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderService {

    private RabbitTemplate template;
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:8000/api/v1/"; //TODO: when dockerized use oms' hostname instead of localhost.


    @Autowired
    public OrderService(RabbitTemplate template) {
        this.template = template;
    }

    public ResponseEntity createOrder(Orders data) {
        data.setStatus(OrderStatus.OPEN);

        // Send open order to persister via RMQ
        template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, data);


        // Ask the actual price from assetcache port 8000.
        Asset asset = restTemplate.getForObject(url + data.getAssetType().toString().toLowerCase() + "/" + data.getSymbol(),Asset.class);

        // Fill the price to the order and update the order in DB.
        data.setPrice(asset.getOpen());
        data.setStatus(OrderStatus.FILLED);
        template.convertAndSend(MQConfig.EXCHANGE, MQConfig.ROUTING_KEY, data);

        // Create position, send to persister RMQ

        // Store both Entity in-memory

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
