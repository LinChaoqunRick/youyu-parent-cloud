package com.youyu.service.message;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.message.MessageListInput;
import com.youyu.dto.message.MessageListOutput;
import com.youyu.entity.user.Message;

import java.util.List;

/**
 * (Message)表服务接口
 *
 * @author makejava
 * @since 2024-02-20 22:04:04
 */
public interface MessageService extends IService<Message> {
    PageOutput<MessageListOutput> pageMessage(MessageListInput input);
    Long countReplies(Long rootId);
    List<MessageListOutput> selectLatestReplies(Long id);
}

