package com.youyu.service.mail.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.mapper.post.FavoritesMapper;
import com.youyu.entity.post.Favorites;
import com.youyu.service.post.FavoritesService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Objects;

/**
 * (Favorites)表服务实现类
 *
 * @author makejava
 * @since 2024-04-28 22:40:18
 */
@Service("favoritesService")
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {

    @Resource
    private FavoritesMapper favoritesMapper;

    @Override
    public boolean isFavoriteOwner(Long favoriteId, Long userId) {
        Favorites favorites = favoritesMapper.selectById(favoriteId);
        return Objects.equals(userId, favorites.getUserId());
    }

    @Override
    public long getFavoriteUserId(Long favoriteId) {
        Favorites favorites = favoritesMapper.selectById(favoriteId);
        return favorites.getUserId();
    }
}

