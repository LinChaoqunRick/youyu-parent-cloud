package com.youyu.mapper.post;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.post.Category;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (BsCategory)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-01 22:31:12
 */
@Mapper
@Repository
public interface CategoryMapper extends BaseMapper<Category> {

}

