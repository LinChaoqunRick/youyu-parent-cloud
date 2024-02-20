package com.youyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.mapper.MessageMapper;
import com.youyu.entity.user.Message;
import com.youyu.service.MessageService;
import org.springframework.stereotype.Service;

/**
 * (Message)表服务实现类
 *
 * @author makejava
 * @since 2024-02-20 22:04:04
 */
@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

}

