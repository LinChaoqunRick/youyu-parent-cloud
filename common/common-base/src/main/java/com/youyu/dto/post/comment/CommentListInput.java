package com.youyu.dto.post.comment;

import com.youyu.dto.common.PageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentListInput extends PageBase {
    @NotNull(message = "文章id不能为空")
    Long postId;
    String orderBy = "create_time";
}
