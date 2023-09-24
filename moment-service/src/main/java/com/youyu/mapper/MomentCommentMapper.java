package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.MomentComment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (MomentComment)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-18 20:28:02
 */
@Mapper
@Repository
public interface MomentCommentMapper extends BaseMapper<MomentComment> {

}

