package com.youyu.filter;

import com.alibaba.fastjson.JSON;
import com.youyu.enums.ResultCode;
import com.youyu.result.ResponseResult;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Mr.M
 * @version 1.0
 * @description 网关认证过虑器
 * @date 2022/9/27 12:10
 */
@Component
@Slf4j
public class GatewayAuthFilter implements GlobalFilter, Ordered {
    //白名单
    private static List<String> whitelist = null;

    @Resource
    private TokenStore tokenStore;

    static {
        //加载白名单
        try (
                InputStream resourceAsStream = GatewayAuthFilter.class.getResourceAsStream("/security-whitelist.properties")
        ) {
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            Set<String> strings = properties.stringPropertyNames();
            whitelist = new ArrayList<>(strings);
        } catch (Exception e) {
            log.error("加载/security-whitelist.properties出错:{}", e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUrl = exchange.getRequest().getPath().value();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        //白名单放行
        for (String url : whitelist) {
            if (pathMatcher.match(url, requestUrl)) {
                return chain.filter(exchange);
            }
        }

        String token = getToken(exchange);
        if (StringUtils.isBlank(token)) {
            return buildReturnMono(HttpStatus.UNAUTHORIZED, ResultCode.UNAUTHORIZED.getMessage(), exchange);
        }

        //判断是否是有效的token
        OAuth2AccessToken oAuth2AccessToken;
        try {
            oAuth2AccessToken = tokenStore.readAccessToken(token);
            boolean expired = oAuth2AccessToken.isExpired();
            if (expired) {
                return buildReturnMono(HttpStatus.UNAUTHORIZED, "认证令牌已过期", exchange);
            }
            return chain.filter(exchange);
        } catch (InvalidTokenException e) {
            log.info("认证令牌无效: {}", token);
            return buildReturnMono(HttpStatus.UNAUTHORIZED, "认证令牌无效", exchange);
        }
    }

    /**
     * 获取token
     */
    private String getToken(ServerWebExchange exchange) {
        String tokenStr = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StringUtils.isBlank(tokenStr)) {
            return null;
        }
        String token = tokenStr.split(" ")[1];
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return token;
    }

    private Mono<Void> buildReturnMono(HttpStatus status, String errorMessage, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        String jsonString = JSON.toJSONString(ResponseResult.error(status.value(), errorMessage));
        byte[] bits = jsonString.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}