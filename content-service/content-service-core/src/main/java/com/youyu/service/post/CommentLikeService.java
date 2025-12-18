package com.youyu.service.post;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.entity.post.CommentLike;

import java.util.List;
import java.util.Set;

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

    /**
     * 批量查询用户点赞的评论ID列表
     * @param userId 用户ID
     * @param commentIds 评论ID列表
     * @return 用户点赞的评论ID集合
     */
    Set<Long> getLikedCommentIds(Long userId, List<Long> commentIds);
}
