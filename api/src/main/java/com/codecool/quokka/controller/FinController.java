package com.codecool.quokka.controller;

import com.codecool.quokka.model.OrderDto;
import com.codecool.quokka.model.assets.AssetType;
import com.codecool.quokka.model.order.Orders;
import com.codecool.quokka.utils.TokenEncoder;
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
    public static final UUID ACCOUNT_ID = UUID.fromString("a1521309-f533-460a-a9fc-3028b0efc79b");
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
        return this.createOrder(data, token, AssetType.STOCK);
    }

    @PostMapping(path = "crypto")
    @PreAuthorize("hasRole('TRADER')")
    public ResponseEntity createNewCryptoOrder(@RequestBody OrderDto data, @RequestHeader("Authorization") String token) {
        return this.createOrder(data, token, AssetType.CRYPTO);
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
