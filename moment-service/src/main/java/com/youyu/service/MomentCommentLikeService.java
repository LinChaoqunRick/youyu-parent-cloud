package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.entity.moment.MomentCommentLike;

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
}

