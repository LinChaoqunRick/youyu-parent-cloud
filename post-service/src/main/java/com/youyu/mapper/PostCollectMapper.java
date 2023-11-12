package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.post.PostCollect;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (PostCollect)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-18 20:26:37
 */
@Mapper
@Repository
public interface PostCollectMapper extends BaseMapper<PostCollect> {


}

