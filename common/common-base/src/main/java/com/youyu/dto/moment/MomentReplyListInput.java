package com.youyu.dto.moment;

import com.youyu.dto.common.PageBase;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class MomentReplyListInput extends PageBase {
    @NotNull(message = "评论id不能为空")
    private Long id;
    String orderBy = "create_time";
}
