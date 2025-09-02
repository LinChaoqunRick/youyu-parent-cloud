package com.youyu.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 时刻通知消息队列配置
 */

@Configuration
@EnableRabbit
public class MomentRabbitConfiguration {

    @Bean("momentCommentMailQueue")     //定义消息队列
    public Queue queue() {
        return QueueBuilder
                .durable("momentCommentMail")   // 队列名称
                .deadLetterExchange("dlx.direct")  // 死信交换机名称
                .deadLetterRoutingKey("dl-MomentComment") // 死信路由键
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