package com.youyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.message.MessageUserOutput;
import com.youyu.entity.user.User;
import com.youyu.mapper.MessageMapper;
import com.youyu.entity.user.Message;
import com.youyu.service.MessageService;
import com.youyu.service.UserService;
import com.youyu.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * (Message)表服务实现类
 *
 * @author makejava
 * @since 2024-02-20 22:04:04
 */
@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    private UserService userService;

    @Override
    public MessageUserOutput getUserDetail(Long userId) {
        User user = userService.getById(userId);
        MessageUserOutput userOutput = BeanCopyUtils.copyBean(user, MessageUserOutput.class);
        return userOutput;
    }
}

