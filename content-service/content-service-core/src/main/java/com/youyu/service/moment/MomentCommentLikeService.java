package com.youyu.service.moment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.entity.moment.MomentCommentLike;

import java.util.List;
import java.util.Set;

/**
 * (MomentCommentLike)表服务接口
 *
 * @author makejava
 * @since 2023-07-10 22:25:25
 */
public interface MomentCommentLikeService extends IService<MomentCommentLike> {
    boolean setMomentCommentLike(MomentCommentLike input);
    boolean isMomentCommentLike(MomentCommentLike input);
    boolean cancelMomentCommentLike(MomentCommentLike input);
    void rectifySupportCount();

    /**
     * 批量查询用户点赞的评论ID列表
     * @param userId 用户ID
     * @param commentIds 评论ID列表
     * @return 用户点赞的评论ID集合
     */
    Set<Long> getLikedCommentIds(Long userId, List<Long> commentIds);
}

