package com.youyu.service.moment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.moment.MomentUserOutput;
import com.youyu.dto.page.PageOutput;
import com.youyu.dto.moment.MomentLikeUserListInput;
import com.youyu.entity.moment.MomentLike;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * 批量查询用户点赞的时刻ID列表
     * @param userId 用户ID
     * @param momentIds 时刻ID列表
     * @return 用户点赞的时刻ID集合
     */
    Set<Long> getLikedMomentIds(Long userId, List<Long> momentIds);

    /**
     * 批量查询时刻的点赞用户列表
     * @param momentIds 时刻ID列表
     * @param limit 每个时刻返回的最大点赞用户数
     * @return momentId -> 点赞用户列表的映射
     */
    Map<Long, List<MomentUserOutput>> batchGetLikeUsers(List<Long> momentIds, int limit);
}

