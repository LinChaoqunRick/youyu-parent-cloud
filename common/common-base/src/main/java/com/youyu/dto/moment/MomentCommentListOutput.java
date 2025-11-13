package com.youyu.dto.moment;

import com.youyu.dto.user.ActorBase;
import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.user.Actor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MomentCommentListOutput extends MomentComment {
    private Long replyCount = 0L;
    private Actor actor;
    private Actor actorTo;
    private boolean commentLike;
    private List<MomentCommentListOutput> children;
}
