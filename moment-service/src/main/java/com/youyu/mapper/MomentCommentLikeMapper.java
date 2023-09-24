package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.MomentCommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (MomentCommentLike)表数据库访问层
 *
 * @author makejava
 * @since 2023-07-10 22:25:25
 */
@Mapper
@Repository
public interface MomentCommentLikeMapper extends BaseMapper<MomentCommentLike> {

}

