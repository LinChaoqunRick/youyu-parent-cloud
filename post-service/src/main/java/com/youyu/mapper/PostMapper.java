package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.post.Post;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * (Post)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-01 16:28:22
 */
@Mapper
@Repository
public interface PostMapper extends BaseMapper<Post> {

}

