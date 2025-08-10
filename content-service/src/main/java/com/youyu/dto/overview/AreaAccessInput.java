package com.youyu.dto.overview;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class AreaAccessInput {
    @NotNull(message = "开始时间不能为空")
    private Date startTime;

    @NotNull(message = "结束时间不能为空")
    private Date endTime;
}
