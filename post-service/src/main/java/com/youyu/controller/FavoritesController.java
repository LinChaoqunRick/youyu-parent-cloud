package com.youyu.controller;

import com.youyu.dto.post.post.PostListOutput;
import com.youyu.entity.post.Favorites;
import com.youyu.mapper.PostMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.FavoritesService;
import com.youyu.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * (Favorites)表控制层
 *
 * @author makejava
 * @since 2024-04-28 22:40:11
 */
@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    @Resource
    private FavoritesService favoritesService;

    @Resource
    private PostMapper postMapper;

    @RequestMapping("/create")
    public ResponseResult<Boolean> create(@RequestBody Favorites input) {
        Long userId = SecurityUtils.getUserId();
        input.setUserId(userId);
        boolean save = favoritesService.save(input);
        return ResponseResult.success(save);
    }

    @RequestMapping("/update")
    public ResponseResult<Boolean> update(@RequestBody Favorites input) {
        boolean update = favoritesService.updateById(input);
        return ResponseResult.success(update);
    }

    @RequestMapping("/delete")
    public ResponseResult<Boolean> delete(@RequestParam Long id) {
        boolean remove = favoritesService.removeById(id);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/page")
    public ResponseResult<Boolean> page(@RequestParam Long id) {
        Favorites favorites = favoritesService.getById(id);
        List<PostListOutput> outputs = postMapper.listPostByIds(favorites.getPostIds());
        boolean remove = favoritesService.removeById(id);
        return ResponseResult.success(remove);
    }
}

