package com.codecool.quokka.oms.controller;

import com.codecool.quokka.model.order.AssetOrder;
import com.codecool.quokka.model.order.OrderType;
import com.codecool.quokka.oms.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity createOrder(@RequestBody AssetOrder data) {
        return orderService.createOrder(data);
//        if (data.getType().equals(OrderType.LIMIT)){
//            return new ResponseEntity<>("Limit order not supported yet", HttpStatus.BAD_REQUEST);
//        }
//        System.out.println("Hello from OMS" + data);
//        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
