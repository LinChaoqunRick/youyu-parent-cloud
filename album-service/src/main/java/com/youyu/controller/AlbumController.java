package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.AlbumListInput;
import com.youyu.dto.AlbumListOutput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.Album;
import com.youyu.result.ResponseResult;
import com.youyu.service.AlbumService;
import com.youyu.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * (Album)表控制层
 *
 * @author makejava
 * @since 2024-06-02 13:48:59
 */
@RestController
@RequestMapping("album")
public class AlbumController {

    @Resource
    private AlbumService albumService;

    @RequestMapping("/open/list")
    public ResponseResult<PageOutput<AlbumListOutput>> list(AlbumListInput input) {
        // 分页查询
        Page<Album> page = new Page<>(input.getPageNum(), input.getPageSize());
        PageOutput<AlbumListOutput> pageOutput = albumService.selectPage(page, input.getName());
        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/create")
    public ResponseResult<Boolean> create(@Valid Album input) {
        boolean save = albumService.save(input);
        return ResponseResult.success(save);
    }

    @RequestMapping("/update")
    public ResponseResult<Boolean> update(@Valid Album input) {
        Album album = albumService.getById(input.getId());
        // 水平越权校验
        SecurityUtils.authAuthorizationUser(album.getUserId());
        boolean update = albumService.updateById(input);
        return ResponseResult.success(update);
    }

    @RequestMapping("/remove")
    public ResponseResult<Boolean> remove(@Valid Album input) {
        Album album = albumService.getById(input.getId());
        // 水平越权校验
        SecurityUtils.authAuthorizationUser(album.getUserId());
        boolean update = albumService.removeById(input.getId());
        return ResponseResult.success(update);
    }
}

