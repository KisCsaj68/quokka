package com.codecool.quokka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class QuokkaApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuokkaApplication.class, args);
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
    }

}
