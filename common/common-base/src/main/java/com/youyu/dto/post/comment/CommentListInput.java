package com.youyu.dto.post.comment;

import com.youyu.dto.common.PageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentListInput extends PageBase {
    Long postId;
    Long rootId;
    String orderBy = "create_time";
}
