package com.youyu.dto.moment;

import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.moment.MomentUserOutput;
import lombok.Data;

@Data
public class MomentCommentListOutput extends MomentComment {
    private Integer replyCount = 0;

    private MomentUserOutput user;

    private MomentUserOutput userTo;

    private boolean commentLike;
}
