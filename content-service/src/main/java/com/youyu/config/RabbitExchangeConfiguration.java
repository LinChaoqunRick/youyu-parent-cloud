package com.youyu.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitExchangeConfiguration {
    @Bean("directExchange")
    public Exchange exchange() {
        //定义交换机Bean，可以很多个
        return ExchangeBuilder.directExchange("amq.direct").build();
    }

    @Bean("directDlExchange")
    public Exchange dlExchange() {
        //创建一个新的死信交换机
        return ExchangeBuilder.directExchange("dlx.direct").build();
    }
}
