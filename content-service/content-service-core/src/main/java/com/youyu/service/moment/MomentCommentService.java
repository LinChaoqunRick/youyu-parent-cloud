package com.youyu.service.moment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.page.PageOutput;
import com.youyu.dto.moment.MomentCommentListInput;
import com.youyu.dto.moment.MomentCommentOutput;
import com.youyu.entity.moment.MomentComment;

import java.util.List;
import java.util.Map;

/**
 * (MomentComment)表服务接口
 *
 * @author makejava
 * @since 2023-06-18 20:28:05
 */
public interface MomentCommentService extends IService<MomentComment> {
    MomentCommentOutput createComment(MomentComment input);
    PageOutput<MomentCommentOutput> momentCommentPage(MomentCommentListInput input);
    int getCommentCountByMomentId(Long momentId);
    boolean deleteComment(Long momentId);

    /**
     * 批量查询时刻的评论数量
     * @param momentIds 时刻ID列表
     * @return momentId -> 评论数量的映射
     */
    Map<Long, Integer> batchGetCommentCount(List<Long> momentIds);
}

