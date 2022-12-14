package com.codecool.quokka.oms.controller;

import com.codecool.quokka.oms.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal")
public class InternalController {

    private OrderService orderService;

    @Autowired
    public InternalController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(path = "open-orders")
    public void pushOrders() {
        orderService.pushOrders();
    }
}
