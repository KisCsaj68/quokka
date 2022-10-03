package com.codecool.quokka.oms.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @PostMapping
    public void createOrder(@RequestBody HashMap<String, String> data){
        System.out.println(data);
    }


}
