package com.youyu.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文章通知消息队列配置
 */

@Configuration
@EnableRabbit
public class PostRabbitConfiguration {

    @Bean("postCommentMailQueue")     //定义消息队列
    public Queue queue() {
        return QueueBuilder
                .durable("postCommentMail")   //非持久化类型
                .deadLetterExchange("dlx.direct")
                .deadLetterRoutingKey("dl-PostComment")
                .build();
    }

    @Bean("postMQBinding")
    public Binding binding(@Qualifier("directExchange") Exchange exchange,
                           @Qualifier("postCommentMailQueue") Queue queue) {
        //将我们刚刚定义的交换机和队列进行绑定
        return BindingBuilder
                .bind(queue)   //绑定队列
                .to(exchange)  //到交换机
                .with("postCommentMail")   //使用自定义的routingKey
                .noargs();
    }

    @Bean("dl-PostCommentQueue")   //创建一个新的死信队列
    public Queue dlQueue() {
        return QueueBuilder
                .durable("dl-PostComment")
                .build();
    }

    @Bean("dlPostMQBinding")   //死信交换机和死信队列进绑定
    public Binding dlBinding(@Qualifier("directDlExchange") Exchange exchange,
                             @Qualifier("dl-PostCommentQueue") Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("dl-PostComment")
                .noargs();
    }
}
