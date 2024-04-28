package com.youyu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.mapper.FavoritesMapper;
import com.youyu.entity.post.Favorites;
import com.youyu.service.FavoritesService;
import org.springframework.stereotype.Service;

/**
 * (Favorites)表服务实现类
 *
 * @author makejava
 * @since 2024-04-28 22:40:18
 */
@Service("favoritesService")
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {

}

