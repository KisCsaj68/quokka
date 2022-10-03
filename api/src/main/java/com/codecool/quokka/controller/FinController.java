package com.codecool.quokka.controller;

import com.codecool.quokka.model.OrderDto;
import com.codecool.quokka.model.order.Order;
import com.codecool.quokka.model.order.OrderType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
public class FinController {
    public static final UUID ACCOUNT_ID = UUID.fromString("a1521309-f533-460a-a9fc-3028b0efc79b");
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:8081/api/v1/order"; //TODO: when dockerized use oms' hostname instead of localhost.

    @PostMapping(path = "/api/v1/order")
    public ResponseEntity createNewOrder(@RequestBody OrderDto data) {

        Order actualOrder = data.toEntity(ACCOUNT_ID);
        if (data.getType().equals(OrderType.LIMIT)) {
            throw new UnsupportedOperationException("Limit order not supported yet");
            // post request to OMS / LimitOrder
        }
        //post request to OMS / marketOrder
        HttpEntity<Order> request = new HttpEntity<>(actualOrder);
        System.out.println(request.getBody());
        String orderCreateResponse = restTemplate.postForObject(url, request, String.class);
        return new ResponseEntity<>(orderCreateResponse, HttpStatus.OK);
    }
}
