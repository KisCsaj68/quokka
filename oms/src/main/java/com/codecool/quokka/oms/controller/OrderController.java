package com.codecool.quokka.oms.controller;

import com.codecool.quokka.model.order.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @PostMapping
    public ResponseEntity createOrder(@RequestBody Order data) {
        // Send open order to persister via RMQ
        // Ask the actual price from assetcache
        // Fill the price to the order and update the order in DB.
        // Create position, send to persister RMQ
        // Store both Entity in-memory
        System.out.println("Hello from OMS" + data);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
