package com.youyu.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class MessageListOutput {
    private Long id;
    private Long rootId;
    private Long userId;
    private String nickname;
    private String avatar;
    private String content;
    private Integer adcode;
    private String adname;
    private Integer status;
    private Long supportCount;
    private Long opposeCount;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
