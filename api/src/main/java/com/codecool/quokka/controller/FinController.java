package com.codecool.quokka.controller;

import com.codecool.quokka.model.OrderDto;
import com.codecool.quokka.model.assets.AssetType;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.utils.TokenEncoder;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@ConfigurationProperties
public class FinController {
    private static final Counter order_request = Counter.build().namespace("quokka").subsystem("api")
            .name("order_request")
            .labelNames("operation", "asset_type")
            .help("total number of order placement").register();

    private static final Histogram order_request_time_duration = Histogram.build().namespace("quokka").subsystem("api")
            .name("order_request_time_duration")
            .labelNames("operation", "asset_type")
            .help("total elapsed time from request to response").register();
    private RestTemplate restTemplate = new RestTemplate();

    private final TokenEncoder tokenEncoder;

    @Autowired
    public FinController(TokenEncoder tokenEncoder) {
        this.tokenEncoder = tokenEncoder;
    }

    @Value("${quokka.service.oms.address}${quokka.service.oms.endpoint}")
    private String url;

    @PostMapping(path = "stock")
    @PreAuthorize("hasRole('TRADER')")
    public ResponseEntity createNewStockOrder(@RequestBody OrderDto data, @RequestHeader("Authorization") String token) {
        order_request.labels("write", "stock").inc();
        Histogram.Timer timer = order_request_time_duration.labels("write", "stock").startTimer();
        try {

            return this.createOrder(data, token, AssetType.STOCK);
        }
        finally {
            timer.observeDuration();
        }
    }

    @PostMapping(path = "crypto")
    @PreAuthorize("hasRole('TRADER')")
    public ResponseEntity createNewCryptoOrder(@RequestBody OrderDto data, @RequestHeader("Authorization") String token) {
        order_request.labels("write", "crypto").inc();
        Histogram.Timer timer = order_request_time_duration.labels("write", "crypto").startTimer();
        try {

            return this.createOrder(data, token, AssetType.CRYPTO);
        }
        finally {
            timer.observeDuration();
        }
    }

    private ResponseEntity createOrder(OrderDto data, String token, AssetType type) {
        Orders actualOrders = data.toEntity(tokenEncoder.getUserId(token));
        actualOrders.setAssetType(type);
        // post request to OMS
        HttpEntity<Orders> request = new HttpEntity<>(actualOrders);
        ResponseEntity orderCreateResponse = restTemplate.postForObject(url, request, ResponseEntity.class);
        return orderCreateResponse;
    }
}
