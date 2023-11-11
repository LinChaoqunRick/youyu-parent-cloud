package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.post.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (CommentLike)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-06 21:51:09
 */
@Mapper
@Repository
public interface CommentLikeMapper extends BaseMapper<CommentLike> {


}

