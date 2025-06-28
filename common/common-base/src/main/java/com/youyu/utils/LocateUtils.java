package com.youyu.utils;

import com.youyu.entity.user.PositionInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Component
public class LocateUtils {

    @Resource
    private RestTemplate restTemplate;

    @Value("${amap.key}")
    private String amapKey;

    public PositionInfo getUserPositionByIP() {
        String clientIp = RequestUtils.getClientIp();
        return restTemplate.getForObject("https://restapi.amap.com/v3/ip?key=" + amapKey + "&ip=" + clientIp, PositionInfo.class);
    }
}
