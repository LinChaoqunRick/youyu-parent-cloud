package com.youyu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {

    @GetMapping("/gateway/test")
    public String gatewayTest() {
        return "/gateway/test";
    }
}
