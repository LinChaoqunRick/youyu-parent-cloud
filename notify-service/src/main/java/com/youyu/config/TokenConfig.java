package com.youyu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.Resource;

/**
 * @author Administrator
 * @version 1.0
 **/
@Configuration
public class TokenConfig {

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("MtR6UDKvOg7IBcpW7o4j0UK2pVAf1geB14VXicSOm92quFmDhtOjo9nDTxajysqyWlfXKIqqGcsTHBGnBeZLZ0aQfHJS8a22P2UgYJ47vrNesKZ7UGSCnLeKELunVt6lSz3KZ5F1rA11XHZgoLXsTjwEtHPylkISG75Q7L9jeKbAoDGRgYEl2r8V8ijvAqmg3OyxOXaMS6IwgLTBFZJfpiQQJ4I1lO5oqpQ5gqK7aLk5SgdU1xPPyfeNyseMaxkY");
        return jwtAccessTokenConverter;
    }
}
