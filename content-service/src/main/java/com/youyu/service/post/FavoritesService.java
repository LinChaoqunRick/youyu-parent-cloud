package com.youyu.service.post;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.entity.post.Favorites;

/**
 * (Favorites)表服务接口
 *
 * @author makejava
 * @since 2024-04-28 22:40:17
 */
public interface FavoritesService extends IService<Favorites> {
    boolean isFavoriteOwner(Long favoriteId, Long userId);
    long getFavoriteUserId(Long favoriteId);
}

