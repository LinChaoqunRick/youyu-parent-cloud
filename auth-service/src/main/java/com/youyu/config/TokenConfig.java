package com.youyu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author Administrator
 * @version 1.0
 **/
@Configuration
public class TokenConfig {

    @Resource
    TokenStore tokenStore;

    @Resource
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Resource
    private CustomTokenEnhancer customTokenEnhancer;

    @Resource
    private AuthenticationManager authenticationManagerBean;

    //令牌管理服务
    @Bean(name = "authorizationServerTokenServicesCustom")
    public DefaultTokenServices tokenService() {
        DefaultTokenServices service = new DefaultTokenServices();

        service.setAuthenticationManager(authenticationManagerBean);
        service.setSupportRefreshToken(true);//支持刷新令牌
        service.setTokenStore(tokenStore);//令牌存储策略
        service.setReuseRefreshToken(false);//每次都刷新refresh_token

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(customTokenEnhancer, jwtAccessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);

        service.setAccessTokenValiditySeconds(3600 * 12); // 令牌默认有效期12小时
        service.setRefreshTokenValiditySeconds(3600 * 24 * 7); // 刷新令牌默认有效期3天
        return service;
    }

}
