package com.youyu.config;

import com.youyu.filter.JwtAuthenticationTokenFilter;
import com.youyu.service.impl.UserDetailsServiceImpl;
import com.youyu.authentication.sms.SmsCodeAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 短信验证码登录
     *
     * @return
     */
    @Bean
    public SmsCodeAuthenticationProvider smsCodeAuthenticationProvider() {
        SmsCodeAuthenticationProvider smsCodeAuthenticationProvider = new SmsCodeAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(userDetailsServiceImpl);
        return smsCodeAuthenticationProvider;
    }

    /**
     * 账号密码验证
     *
     * @return
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        //对默认的UserDetailsService进行覆盖
        authenticationProvider.setUserDetailsService(userDetailsServiceImpl);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    // 登陆服务需要认证的接口
    private final String[] LOGIN_AUTH_PATHS = {
            "/login/logout",
    };
    // 文字服务需要认证的接口
    private final String[] POST_AUTH_PATHS = {
            "/post/setPostLike",
            "/post/isPostLike",
            "/post/cancelPostLike",
            "/post/edit/detail",
            "/post/delete",
            "/post/update"
    };
    // 笔记服务需要认证的接口
    private final String[] NOTE_AUTH_PATHS = {
            "/note/create",
            "/note/update",
            "/note/delete",
            "/noteChapter/create",
            "/noteChapter/update",
            "/noteChapter/delete"
    };
    // oss服务需要认证的接口
    private final String[] OSS_AUTH_PATHS = {
            "/oss/policy"
    };
    // 时刻服务需要认证的接口
    private final String[] MOMENT_AUTH_PATHS = {
            "/moment/create",
            "/moment/delete",
            "/momentLike/setMomentLike",
            "/momentLike/cancelMomentLike",
            "/momentCommentLike/setMomentCommentLike",
            "/momentCommentLike/cancelMomentCommentLike",
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/login/accountLogin").anonymous()
                .antMatchers(LOGIN_AUTH_PATHS).authenticated()
                .antMatchers(POST_AUTH_PATHS).authenticated()
                .antMatchers(NOTE_AUTH_PATHS).authenticated()
                .antMatchers(OSS_AUTH_PATHS).authenticated()
                .antMatchers(MOMENT_AUTH_PATHS).authenticated()
                // 除上面外的所有请求全部需要鉴权认证
                // .anyRequest().authenticated();
                .anyRequest().permitAll();

        // 添加过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 配置异常处理器
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        http.authenticationProvider(smsCodeAuthenticationProvider());
        http.authenticationProvider(authenticationProvider());

        // 允许跨域
        http.cors();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
