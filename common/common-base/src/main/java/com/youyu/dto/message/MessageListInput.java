package com.youyu.dto.message;

import com.youyu.dto.common.PageBase;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class MessageListInput extends PageBase {
    private Integer status;

    private String keyword;

    private Long rootId = -1L;

    @DateTimeFormat(pattern = "yyyy-MM-dd") // 解析字符串成 Date
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd") // 解析字符串成 Date
    private Date endTime;
}
