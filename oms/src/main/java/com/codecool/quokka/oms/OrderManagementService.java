package com.codecool.quokka.oms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.codecool.quokka.model")
public class OrderManagementService {
    public static void main(String[] args) {
        SpringApplication.run(OrderManagementService.class, args);
//        System.out.println("HelloWorld");
//        Thread.sleep(1000 * 1000);
    }
}
