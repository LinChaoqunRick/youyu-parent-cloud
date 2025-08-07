package com.youyu.service.message.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.message.MessageUserOutput;
import com.youyu.entity.user.User;
import com.youyu.entity.user.Message;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.message.MessageMapper;
import com.youyu.service.message.MessageService;
import com.youyu.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * (Message)表服务实现类
 *
 * @author makejava
 * @since 2024-02-20 22:04:04
 */
@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    private UserServiceClient userServiceClient;

    @Override
    public MessageUserOutput getUserDetail(Long userId) {
        User user = userServiceClient.selectById(userId).getData();
        return BeanCopyUtils.copyBean(user, MessageUserOutput.class);
    }
}

