package com.youyu.dto.post.comment;

import com.youyu.entity.post.Comment;
import com.youyu.entity.user.Actor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentListOutput extends Comment {
    /**
     * 子评论数量
     */
    private Long replyCount;

    /**
     * 发布人
     */
    private Actor actor;

    /**
     * 回复人
     */
    private Actor actorTo;
    /**
     * 当前用户点赞信息
     */
    private boolean commentLike;
    /**
     * 回复数据
     */
    private List<CommentListOutput> children;
}
