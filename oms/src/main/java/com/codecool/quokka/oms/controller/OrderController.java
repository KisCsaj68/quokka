package com.codecool.quokka.oms.controller;

import com.codecool.quokka.model.assets.AssetType;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.oms.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity createStockOrder(@RequestBody Orders data) {
        return orderService.createOrder(data);
    }
}
