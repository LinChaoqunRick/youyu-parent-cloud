package com.youyu.dto.comment;

import com.youyu.dto.page.PageBase;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class PostReplyListInput extends PageBase {
    @NotNull(message = "评论id不能为空")
    Long commentId;
    String orderBy = "create_time";
}
