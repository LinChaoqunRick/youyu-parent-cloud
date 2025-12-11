package com.youyu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestBeanConfig {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
