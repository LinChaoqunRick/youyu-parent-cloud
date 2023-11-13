package com.youyu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GatewayServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApp.class, args);
    }
}
