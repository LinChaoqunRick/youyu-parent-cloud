package com.youyu.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncExecutorConfig {

    @Bean
    public Executor taskExecutor() {
        ExecutorService delegate = Executors.newFixedThreadPool(10);
        return new DelegatingSecurityContextExecutorService(delegate);
    }
}
