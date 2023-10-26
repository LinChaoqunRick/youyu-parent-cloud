package com.youyu.config;

import com.youyu.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import javax.annotation.Resource;

/**
 * @author
 * @version 1.0
 * @description 授权服务器配置
 * @date
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Resource(name = "authorizationServerTokenServicesCustom")
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    //客户端详情服务
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()   //这里我们直接硬编码创建，当然也可以像Security那样自定义或是使用JDBC从数据库读取
                .withClient("web")   //客户端名称，随便起就行
                .secret(passwordEncoder.encode("654321"))      //只与客户端分享的secret，随便写，但是注意要加密
                .autoApprove(false)    //自动审批，这里关闭，要的就是一会体验那种感觉
                .scopes("all")     //授权范围，这里我们使用全部all
                .authorizedGrantTypes("client_credentials", "password", "implicit", "authorization_code", "refresh_token")
                .redirectUris("http://localhost/8080/login");//客户端接收授权码的重定向地址;
    }


    //令牌端点的访问配置
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager)//认证管理器
                .tokenServices(authorizationServerTokenServices);//令牌管理服务
    }

    //令牌端点的安全配置
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                .tokenKeyAccess("permitAll()")                    //oauth/token_key是公开
                .checkTokenAccess("permitAll()")                  //oauth/check_token公开
                .allowFormAuthenticationForClients()                //表单认证（申请令牌）
        ;
    }

}
