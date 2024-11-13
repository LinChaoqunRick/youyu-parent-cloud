package com.youyu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RefreshScope
public class GatewayController {

    @GetMapping("/gateway/test")
    public String gatewayTest() {
        return "/gateway/test";
    }
}
