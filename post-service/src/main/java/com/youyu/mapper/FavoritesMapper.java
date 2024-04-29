package com.youyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youyu.entity.post.Favorites;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * (Favorites)表数据库访问层
 *
 * @author makejava
 * @since 2024-04-28 22:40:11
 */
@Mapper
@Repository
public interface FavoritesMapper extends BaseMapper<Favorites> {

}

