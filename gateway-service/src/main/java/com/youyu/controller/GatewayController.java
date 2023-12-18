package com.youyu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RefreshScope
public class GatewayController {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @GetMapping("/gateway/test")
    public String gatewayTest() {
        log.info(datasourceUrl);
        return "/gateway/test" + datasourceUrl;
    }
}
