package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.MomentLikeUserListInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.MomentLike;
import com.youyu.entity.MomentUserOutput;

import java.util.List;

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

