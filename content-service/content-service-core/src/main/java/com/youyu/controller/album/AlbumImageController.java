package com.youyu.controller.album;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.annotation.Log;
import com.youyu.dto.album.AlbumImageListInput;
import com.youyu.dto.album.AlbumImageListOutput;
import com.youyu.dto.album.AlbumImageSaveInput;
import com.youyu.dto.oss.OssBatchSignedUrlInput;
import com.youyu.dto.oss.OssSignedUrlInput;
import com.youyu.dto.page.PageOutput;
import com.youyu.entity.album.Album;
import com.youyu.entity.album.AlbumImage;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.OssServiceClient;
import com.youyu.result.ResponseResult;

import com.youyu.service.album.AlbumImageService;
import com.youyu.service.album.AlbumService;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import lombok.Data;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * (AlbumImage)表控制层
 *
 * @author makejava
 * @since 2024-06-03 21:06:18
 */
@RefreshScope
@Data
@RestController
@RequestMapping("albumImage")
public class AlbumImageController {

    @Resource
    private AlbumImageService albumImageService;

    @Resource
    private AlbumService albumService;

    @Resource
    private OssServiceClient ossServiceClient;

    @RequestMapping("/open/list")
    public ResponseResult<PageOutput<AlbumImageListOutput>> list(@Valid AlbumImageListInput input) {
        LambdaQueryWrapper<AlbumImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlbumImage::getAlbumId, input.getId());
        queryWrapper.orderByDesc(AlbumImage::getCreateTime);

        Album album = albumService.getById(input.getId());
        // 私密，且不是作者或授权用户
        if (album.getOpen() == 0 && !isAccessible(album, SecurityUtils.getUserId())) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }

        Page<AlbumImage> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<AlbumImage> imagePage = albumImageService.page(page, queryWrapper);
        PageOutput<AlbumImageListOutput> pageOutput = PageUtils.setPageResult(imagePage, AlbumImageListOutput.class);

        // 生成url路径
        generateOSSUrl(pageOutput.getList());

        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/open/detail")
    public ResponseResult<AlbumImage> save(@RequestParam Long id) {
        AlbumImage albumImage = albumImageService.getById(id);
        return ResponseResult.success(albumImage);
    }

    @RequestMapping("/create")
    @Log(title = "新增相册图片", type = LogType.INSERT)
    public ResponseResult<List<Long>> create(@Valid @RequestBody AlbumImageSaveInput input) {
        Album album = albumService.getById(input.getAlbumId());
        SecurityUtils.authAuthorizationUser(album.getUserId());
        input.getImages().forEach(image -> image.setAlbumId(input.getAlbumId()));
        boolean save = albumImageService.saveBatch(input.getImages());
        List<Long> ids = input.getImages().stream()
                .map(AlbumImage::getId)
                .collect(Collectors.toList());
        return ResponseResult.success(ids);
    }

    @RequestMapping("/update")
    @Log(title = "编辑相册图片", type = LogType.UPDATE)
    public ResponseResult<Boolean> update(AlbumImage input) {
        Album album = albumService.getById(input.getAlbumId());
        SecurityUtils.authAuthorizationUser(album.getUserId());
        boolean update = albumImageService.updateById(input);
        return ResponseResult.success(update);
    }

    @RequestMapping("/remove")
    @Log(title = "删除相册图片", type = LogType.DELETE)
    public ResponseResult<Boolean> remove(String ids) {
        //TODO... 如何进行水平越权校验？
        List<String> idsList = Arrays.stream(ids.split(",")).toList();
        boolean removed = albumImageService.removeBatchByIds(idsList);
        return ResponseResult.success(removed);
    }

    @RequestMapping("/open/origin")
    // @Log(title = "查看原图", type = LogType.OTHER)
    public ResponseResult<String> getOriginUrl(@RequestParam Long id) {
        AlbumImage albumImage = albumImageService.getById(id);

        // 通过 Feign 调用 infra-service 生成签名 URL
        OssSignedUrlInput input = new OssSignedUrlInput();
        input.setPath(albumImage.getPath());
        input.setExpireSeconds(600L); // 10分钟
        input.setBucket("album"); // 使用相册 bucket

        String signedUrl = ossServiceClient.generateSignedUrl(input).getData();
        return ResponseResult.success(signedUrl);
    }

    public boolean isAccessible(Album album, Long userId) {
        if (Objects.equals(album.getUserId(), userId)) {
            return true;
        }
        List<String> numberList = Arrays.asList(album.getAuthorizedUsers().split(","));
        return numberList.contains(String.valueOf(userId));
    }

    /**
     * 生成oss的带权限的url
     *
     * @param imageList 图片列表;
     */
    public void generateOSSUrl(List<AlbumImageListOutput> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            return;
        }

        // 批量生成缩略图签名 URL
        OssBatchSignedUrlInput input = new OssBatchSignedUrlInput();
        input.setPaths(imageList.stream().map(AlbumImageListOutput::getPath).collect(Collectors.toList()));
        input.setExpireSeconds(600L); // 10分钟
        input.setProcess("style/thumbnail");
        input.setBucket("album"); // 使用相册 bucket

        Map<String, String> urlMap = ossServiceClient.generateBatchSignedUrl(input).getData();

        // 设置 URL
        imageList.forEach(item -> {
            String signedUrl = urlMap.get(item.getPath());
            if (signedUrl != null) {
                item.setUrl(signedUrl);
            }
        });
    }
}

