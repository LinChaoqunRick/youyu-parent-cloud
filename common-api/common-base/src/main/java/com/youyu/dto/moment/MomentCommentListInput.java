package com.youyu.dto.moment;

import com.youyu.dto.common.PageBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MomentCommentListInput extends PageBase {
    @NotNull(message = "时刻id不能为空")
    private Long momentId;
    String orderBy = "create_time";
}
