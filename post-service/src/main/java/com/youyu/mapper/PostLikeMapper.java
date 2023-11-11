package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.post.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (PostLike)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-19 17:01:41
 */
@Mapper
@Repository
public interface PostLikeMapper extends BaseMapper<PostLike> {

}

