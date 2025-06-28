package com.youyu.dto.message;

import com.youyu.entity.user.Message;
import lombok.Data;

@Data
public class MessageListOutput extends Message {
    private String adname;
    private MessageUserOutput userInfo;
}
