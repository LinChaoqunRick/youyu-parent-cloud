package com.youyu.service.post;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.entity.post.CommentLike;

/**
 * (CommentLike)表服务接口
 *
 * @author makejava
 * @since 2023-03-06 21:51:09
 */
public interface CommentLikeService extends IService<CommentLike> {
    boolean setPostCommentLike(CommentLike commentLike);
    boolean isPostCommentLike(CommentLike commentLike);
    boolean cancelPostCommentLike(CommentLike commentLike);
    Long getSupportCount(Long commentId);
    void rectifySupportCount();
}
