package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.AlbumListInput;
import com.youyu.dto.AlbumListOutput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.entity.Album;
import com.youyu.entity.AlbumImage;
import com.youyu.entity.user.User;
import com.youyu.feign.UserServiceClient;
import com.youyu.result.ResponseResult;
import com.youyu.service.AlbumImageService;
import com.youyu.service.AlbumService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @RequestMapping("/open/list")
    public ResponseResult<PageOutput<AlbumListOutput>> list(AlbumListInput input) {
        // 分页查询
        Page<Album> page = new Page<>(input.getPageNum(), input.getPageSize());
        PageOutput<AlbumListOutput> pageOutput = albumService.selectPage(page, input.getName());
        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/open/detail")
    public ResponseResult<AlbumListOutput> detail(@RequestParam Long id) {
        Album album = albumService.getById(id);
        List<Long> authorizedUserIds = null;
        if (StringUtils.hasText(album.getAuthorizedUsers())) {
            authorizedUserIds = Arrays.stream(album.getAuthorizedUsers().split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .toList();
        }

        AlbumListOutput output = BeanCopyUtils.copyBean(album, AlbumListOutput.class);

        PostUserOutput detail = albumService.getUserDetailById(output.getUserId());
        output.setUserInfo(detail);

        // 如果是所有者，查询授权用户
        if (Objects.equals(SecurityUtils.getUserId(), album.getUserId())) {
            if (Objects.nonNull(authorizedUserIds)) {
                List<User> users = userServiceClient.listByIds(authorizedUserIds).getData();
                output.setAuthorizedUserList(users);
            }
        }

        // 查询照片数量
        LambdaQueryWrapper<AlbumImage> albumImageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        albumImageLambdaQueryWrapper.eq(AlbumImage::getAlbumId, album.getId());
        long count = albumImageService.count(albumImageLambdaQueryWrapper);
        output.setImageCount(count);

        return ResponseResult.success(output);
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
}

