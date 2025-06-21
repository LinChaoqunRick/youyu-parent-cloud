package com.youyu.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MomentRabbitConfiguration {

    @Bean("momentCommentMailQueue")     //定义消息队列
    public Queue queue() {
        return QueueBuilder
                .durable("momentCommentMail")   //非持久化类型
                .deadLetterExchange("dlx.direct")
                .deadLetterRoutingKey("dl-MomentComment")
                .build();
    }

    @Bean("momentMQBinding")
    public Binding binding(@Qualifier("directExchange") Exchange exchange,
                           @Qualifier("momentCommentMailQueue") Queue queue) {
        //将我们刚刚定义的交换机和队列进行绑定
        return BindingBuilder
                .bind(queue)   //绑定队列
                .to(exchange)  //到交换机
                .with("momentCommentMail")   //使用自定义的routingKey
                .noargs();
    }

    @Bean("dl-MomentCommentQueue")   //创建一个新的死信队列
    public Queue dlQueue() {
        return QueueBuilder
                .durable("dl-MomentComment")
                .build();
    }

    @Bean("dlMomentMQBinding")   //死信交换机和死信队列进绑定
    public Binding dlBinding(@Qualifier("directDlExchange") Exchange exchange,
                             @Qualifier("dl-MomentCommentQueue") Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("dl-MomentComment")
                .noargs();
    }
}