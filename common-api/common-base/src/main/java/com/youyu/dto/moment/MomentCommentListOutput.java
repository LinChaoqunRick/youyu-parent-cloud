package com.youyu.dto.moment;

import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.moment.MomentUserOutput;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MomentCommentListOutput extends MomentComment {
    private Long replyCount = 0L;

    private MomentUserOutput user;

    private MomentUserOutput userTo;

    private boolean commentLike;
}
