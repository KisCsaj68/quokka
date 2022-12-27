package com.codecool.quokka.oms.controller;

import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.oms.metrics.Metrics;
import com.codecool.quokka.oms.service.OrderService;
import io.prometheus.client.Histogram;
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
        Histogram histogram = null;
        switch (data.getType()) {
            case MARKET -> histogram = Metrics.MARKET_ORDER_REQUEST_TIME_DURATION;
            case LIMIT -> histogram = Metrics.LIMIT_ORDER_REQUEST_TIME_DURATION;
        }
        return orderService.createOrder(data, histogram);
    }


}
