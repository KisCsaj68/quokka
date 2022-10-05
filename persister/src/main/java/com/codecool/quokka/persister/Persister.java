package com.codecool.quokka.persister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EntityScan("com.codecool.quokka.model")
public class Persister {
    public static void main(String[] args) {
        SpringApplication.run(Persister.class, args);
//        System.out.println("HelloWorld");
//        Thread.sleep(1000 * 1000);
    }
}
