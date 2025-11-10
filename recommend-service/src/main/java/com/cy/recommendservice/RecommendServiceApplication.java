package com.cy.recommendservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.cy")
@EnableFeignClients
@EnableRabbit
public class RecommendServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendServiceApplication.class, args);
    }
}
