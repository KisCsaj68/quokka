package com.codecool.quokka.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/v1/order")
@RestController
public class FinController {
    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:8081/api/v1/order";

    @PostMapping
    public ResponseEntity createNewOrder(@RequestBody HashMap<String, String> data) {
        if (data.values().contains("limit")) {
            System.out.println(data);
            return null;
            // post request to OMS / LimitOrder
        }
        //post request to OMS / marketOrder
        HttpEntity<HashMap<String, String>> request = new HttpEntity<>(data);
        System.out.println(request.getBody());
        String orderCreateResponse = restTemplate.postForObject(url, request, String.class);
        return new ResponseEntity<>(orderCreateResponse, HttpStatus.OK);
    }
}
