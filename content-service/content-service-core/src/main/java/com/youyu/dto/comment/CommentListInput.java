package com.youyu.dto.comment;

import com.youyu.dto.page.PageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentListInput extends PageBase {
    Long postId;
    Long rootId;
    String orderBy = "create_time";
}
