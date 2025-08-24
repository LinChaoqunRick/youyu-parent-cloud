package com.youyu.dto.logManage;

import com.youyu.dto.common.PageBase;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class LogPageInput extends PageBase {
    @NotNull(message = "类型不能为空")
    private int type;

    private String areaCodes;

    private String keyword;

    @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 解析字符串成 Date
    private Date startTime;

    @NotNull(message = "结束时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 解析字符串成 Date
    private Date endTime;
}
