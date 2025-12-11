package com.youyu.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MessageListOutput {
    private Long id;
    private Long rootId;
    private Long userId;
    private Long visitorId;
    private String nickname;
    private String avatar;
    private String content;
    private Integer adcode;
    private String adname;
    private Integer status;
    private Long supportCount;
    private Long opposeCount;
    private Long replyCount;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    // 回复
    List<MessageListOutput> children;
}
