package com.youyu.dto.user;

import com.youyu.dto.common.PageBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DynamicListInput extends PageBase {
    @NotNull(message = "用户id不能用空")
    private Long userId;
}
