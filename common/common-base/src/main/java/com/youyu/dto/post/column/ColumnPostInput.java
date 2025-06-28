package com.youyu.dto.post.column;

import com.youyu.dto.common.PageBase;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ColumnPostInput extends PageBase {
    @NotNull(message = "专栏id不能为空")
    Long columnId;
}
