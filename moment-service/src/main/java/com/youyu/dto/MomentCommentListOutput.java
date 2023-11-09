package com.youyu.dto;

import com.youyu.entity.MomentComment;
import com.youyu.entity.MomentUserOutput;
import lombok.Data;

@Data
public class MomentCommentListOutput extends MomentComment {
    private Integer replyCount = 0;

    private MomentUserOutput user;

    private MomentUserOutput userTo;

    private boolean commentLike;
}
