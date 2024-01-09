package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.dto.moment.MomentCommentListOutput;
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
    List<MomentCommentListOutput> getCommentCountByMomentId(@Param("momentId") Long momentId);
}

