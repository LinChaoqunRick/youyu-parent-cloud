package com.youyu.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 系统日志消息队列配置
 */
@Configuration
@EnableRabbit
public class LogSaveRabbitConfig {

    @Bean("systemLogQueue")
    public Queue queue() {
        return QueueBuilder
                .durable("systemLog")
                .deadLetterExchange("dlx.direct")
                .deadLetterRoutingKey("dl-SystemLog")
                .build();
    }

    @Bean("logMQBinding")
    public Binding binding(@Qualifier("directExchange") Exchange exchange,
                           @Qualifier("systemLogQueue") Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("systemLog")
                .noargs();
    }

    @Bean("dl-SystemLogQueue")
    public Queue dlQueue() {
        return QueueBuilder
                .durable("dl-SystemLog")
                .build();
    }

    @Bean("dlLogMQBinding")
    public Binding dlBinding(@Qualifier("directDlExchange") Exchange exchange,
                             @Qualifier("dl-SystemLogQueue") Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("dl-SystemLog")
                .noargs();
    }
}