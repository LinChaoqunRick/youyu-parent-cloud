package com.youyu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@RefreshScope
public class GatewayServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApp.class, args);
    }
}
