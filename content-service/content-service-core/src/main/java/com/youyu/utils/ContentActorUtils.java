package com.youyu.utils;

import com.youyu.dto.ActorBase;
import com.youyu.dto.moment.MomentCommentOutput;
import com.youyu.entity.moment.MomentComment;

public class ContentActorUtils {
    /**
     * 获取评论的actor信息
     * 如果userId存在，说明是来自用户的评论，如果visitorId存在，说明是游客的评论
     *
     * @param comment 时刻评论
     * @return id
     */
    public static ActorBase getCommentActor(MomentComment comment) {
        ActorBase input = new ActorBase();
        if (comment.getUserId() != null && comment.getUserId() != -1) {
            input.setActorId(comment.getUserId());
            input.setActorType(0);
        } else {
            input.setActorId(comment.getVisitorId());
            input.setActorType(1);
        }
        return input;
    }

    /**
     * 获取评论的actor信息
     * 如果userId存在，说明是来自用户的评论，如果visitorId存在，说明是游客的评论
     *
     * @param comment 时刻评论
     * @return id
     */
    public static ActorBase getCommentActor(MomentCommentOutput comment) {
        ActorBase input = new ActorBase();
        if (comment.getUserId() != null && comment.getUserId() != -1) {
            input.setActorId(comment.getUserId());
            input.setActorType(0);
        } else {
            input.setActorId(comment.getVisitorId());
            input.setActorType(1);
        }
        return input;
    }
}
