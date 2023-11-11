package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.moment.MomentLikeUserListInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.moment.MomentLike;
import com.youyu.entity.moment.MomentUserOutput;

/**
 * (MomentLike)表服务接口
 *
 * @author makejava
 * @since 2023-07-02 11:20:04
 */
public interface MomentLikeService extends IService<MomentLike> {
    boolean setMomentLike(MomentLike input);

    boolean isMomentLike(MomentLike input);

    boolean cancelMomentLike(MomentLike input);

    PageOutput<MomentUserOutput> likeUsers(MomentLikeUserListInput input);

    void rectifySupportCount();
}

