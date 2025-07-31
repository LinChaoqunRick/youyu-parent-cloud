package com.youyu.controller.album;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.album.AlbumListInput;
import com.youyu.dto.album.AlbumListOutput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.album.Album;
import com.youyu.entity.album.AlbumImage;
import com.youyu.entity.user.User;
import com.youyu.feign.UserServiceClient;
import com.youyu.result.ResponseResult;
import com.youyu.service.album.AlbumImageService;
import com.youyu.service.album.AlbumService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private AlbumImageService albumImageService;

    @Resource
    private UserServiceClient userServiceClient;

    @RequestMapping("/mine/list")
    public ResponseResult<PageOutput<AlbumListOutput>> mineList(AlbumListInput input) {
        // 分页查询
        Page<Album> page = new Page<>(input.getPageNum(), input.getPageSize());
        Album album = BeanCopyUtils.copyBean(input, Album.class);
        album.setUserId(SecurityUtils.getUserId());
        PageOutput<AlbumListOutput> pageOutput = albumService.selectPage(page, album);
        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/open/list")
    public ResponseResult<PageOutput<AlbumListOutput>> list(AlbumListInput input) {
        Page<Album> page = new Page<>(input.getPageNum(), input.getPageSize());
        Album album = BeanCopyUtils.copyBean(input, Album.class);
        if (input.getUserId() != null) {
            album.setUserId(input.getUserId());
        }
        if (SecurityUtils.getUserId() == null || !SecurityUtils.isContextUser(input.getUserId())) {
            album.setOpen(1);
        }
        PageOutput<AlbumListOutput> pageOutput = albumService.selectPage(page, album);
        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/open/detail")
    public ResponseResult<AlbumListOutput> detail(@RequestParam Long id) {
        Album album = albumService.getById(id);
        List<Long> authorizedUserIds = null;
        List<User> users = null;
        if (StringUtils.hasText(album.getAuthorizedUsers())) {
            authorizedUserIds = Arrays.stream(album.getAuthorizedUsers().split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toList();
        }

        if (album.getOpen() == 0) {
            // 没有权限返回null
            if (!(Objects.equals(SecurityUtils.getUserId(), album.getUserId()) || (authorizedUserIds != null && authorizedUserIds.contains(SecurityUtils.getUserId())))) {
                return ResponseResult.success(null);
            }
        }

        // 查询授权用户详情，查询前5个
        if (Objects.nonNull(authorizedUserIds)) {
            List<Long> topFiveUserIds = authorizedUserIds.stream().limit(8).toList();
            users = userServiceClient.listByIds(topFiveUserIds).getData();
        }

        AlbumListOutput output = BeanCopyUtils.copyBean(album, AlbumListOutput.class);

        if (Objects.nonNull(authorizedUserIds)) {
            output.setAuthorizedUserList(users);
        }

        PostUserOutput detail = albumService.getUserDetailById(output.getUserId());
        output.setUserInfo(detail);

        // 查询照片数量
        LambdaQueryWrapper<AlbumImage> albumImageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        albumImageLambdaQueryWrapper.eq(AlbumImage::getAlbumId, album.getId());
        long count = albumImageService.count(albumImageLambdaQueryWrapper);
        output.setImageCount(count);

        return ResponseResult.success(output);
    }

    @RequestMapping("/open/accessible")
    public ResponseResult<Boolean> accessible(@RequestParam Long id) {
        Album album = albumService.getById(id);
        if (album.getOpen() == 1) {
            return ResponseResult.success(true);
        }
        List<Long> authorizedUserIds = null;
        if (StringUtils.hasText(album.getAuthorizedUsers())) {
            authorizedUserIds = Arrays.stream(album.getAuthorizedUsers().split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toList();
        }

        // 没有权限返回null
        if (!(Objects.equals(SecurityUtils.getUserId(), album.getUserId()) || (authorizedUserIds != null && authorizedUserIds.contains(SecurityUtils.getUserId())))) {
            return ResponseResult.success(false);
        } else {
            return ResponseResult.success(true);
        }
    }

    @RequestMapping("/create")
    public ResponseResult<Boolean> create(@Valid Album input) {
        input.setUserId(SecurityUtils.getUserId());
        boolean save = albumService.save(input);
        return ResponseResult.success(save);
    }

    @RequestMapping("/update")
    public ResponseResult<Boolean> update(@Valid Album input) {
        input.setUpdateTime(new Date());
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

    @RequestMapping("/setCover")
    public ResponseResult<Boolean> setCover(@RequestParam Long id, @RequestParam Long coverImageId) {
        LambdaUpdateWrapper<Album> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Album::getId, id);
        updateWrapper.set(Album::getCoverImageId, coverImageId);
        // 水平越权校验
        SecurityUtils.authAuthorizationUser(SecurityUtils.getUserId());
        boolean update = albumService.update(updateWrapper);
        return ResponseResult.success(update);
    }
}

