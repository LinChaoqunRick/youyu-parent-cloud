package com.youyu.service.moment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.moment.MomentUserOutput;
import com.youyu.dto.page.PageOutput;
import com.youyu.dto.moment.MomentLikeUserListInput;
import com.youyu.entity.moment.MomentLike;

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

