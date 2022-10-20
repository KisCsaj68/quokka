package com.codecool.quokka.controller;

import com.codecool.quokka.model.OrderDto;
import com.codecool.quokka.model.assets.AssetType;
import com.codecool.quokka.model.order.Orders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
public class FinController {
    public static final UUID ACCOUNT_ID = UUID.fromString("a1521309-f533-460a-a9fc-3028b0efc79b");
    private RestTemplate restTemplate = new RestTemplate();
    private String url = "http://oms:9000/api/v1/order"; //TODO: when dockerized use oms' hostname instead of localhost.

    @PostMapping(path = "stock")
    public ResponseEntity createNewStockOrder(@RequestBody OrderDto data) {

        Orders actualOrders = data.toEntity(ACCOUNT_ID);
        actualOrders.setAssetType(AssetType.STOCK);
        System.out.println(actualOrders);
//        if (data.getType().equals(OrderType.LIMIT)) {
//            // post request to OMS / LimitOrder
//            throw new UnsupportedOperationException("Limit order not supported yet");
//        }
        //post request to OMS / marketOrder
        HttpEntity<Orders> request = new HttpEntity<>(actualOrders);
        ResponseEntity orderCreateResponse = restTemplate.postForObject(url, request, ResponseEntity.class);
        return orderCreateResponse;
    }

    @PostMapping(path = "crypto")
    public ResponseEntity createNewCryptoOrder(@RequestBody OrderDto data) {

        Orders actualOrders = data.toEntity(ACCOUNT_ID);
        actualOrders.setAssetType(AssetType.CRYPTO);
//        if (data.getType().equals(OrderType.LIMIT)) {
//            // post request to OMS / LimitOrder
//            throw new UnsupportedOperationException("Limit order not supported yet");
//        }
        //post request to OMS / marketOrder
        HttpEntity<Orders> request = new HttpEntity<>(actualOrders);
        ResponseEntity orderCreateResponse = restTemplate.postForObject(url, request, ResponseEntity.class);
        return orderCreateResponse;
    }
}
