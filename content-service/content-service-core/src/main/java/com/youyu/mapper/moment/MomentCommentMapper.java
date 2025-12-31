package com.youyu.mapper.moment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.dto.moment.MomentCommentCountDTO;
import com.youyu.dto.moment.MomentCommentOutput;
import com.youyu.dto.moment.ReplyCountDTO;
import com.youyu.entity.moment.MomentComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (MomentComment)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-18 20:28:02
 */
@Mapper
@Repository
public interface MomentCommentMapper extends BaseMapper<MomentComment> {
    List<MomentCommentOutput> getCommentCountByMomentId(@Param("momentId") Long momentId);

    /**
     * 批量查询根评论的回复数量
     * @param rootIds 根评论ID列表
     * @return 回复数量统计列表
     */
    List<ReplyCountDTO> batchGetReplyCount(@Param("rootIds") List<Long> rootIds);

    /**
     * 批量查询根评论的最新N条子评论
     * @param rootIds 根评论ID列表
     * @param limit 每个根评论返回的最大子评论数
     * @return 子评论列表
     */
    List<MomentComment> batchGetLatestReplies(@Param("rootIds") List<Long> rootIds, @Param("limit") int limit);

    /**
     * 批量查询时刻的评论数量（包括根评论和子评论）
     * @param momentIds 时刻ID列表
     * @return 评论数量统计列表
     */
    List<MomentCommentCountDTO> batchGetMomentCommentCount(@Param("momentIds") List<Long> momentIds);
}

