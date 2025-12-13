package com.youyu.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 评论通知消息队列配置
 */

@Configuration
@EnableRabbit
public class CommentMailRabbitConfig {

    @Bean("commentMailQueue")
    public Queue queue() {
        return QueueBuilder
                .durable("commentMail")
                .deadLetterExchange("dlx.direct")
                .deadLetterRoutingKey("dl-Comment")
                .build();
    }

    @Bean("commentMQBinding")
    public Binding binding(@Qualifier("directExchange") Exchange exchange,
                           @Qualifier("commentMailQueue") Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("commentMail")
                .noargs();
    }

    @Bean("dl-CommentQueue")
    public Queue dlQueue() {
        return QueueBuilder
                .durable("dl-Comment")
                .build();
    }

    @Bean("dlCommentMQBinding")
    public Binding dlBinding(@Qualifier("directDlExchange") Exchange exchange,
                             @Qualifier("dl-CommentQueue") Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("dl-Comment")
                .noargs();
    }
}
