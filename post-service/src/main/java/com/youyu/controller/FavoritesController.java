package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.favorites.FavoritesPageInput;
import com.youyu.dto.post.favorites.FavoritesListOutput;
import com.youyu.dto.post.post.PostListOutput;
import com.youyu.entity.post.Favorites;
import com.youyu.entity.post.Post;
import com.youyu.entity.post.PostCollect;
import com.youyu.entity.user.ProfileMenu;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.PostMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.FavoritesService;
import com.youyu.service.PostCollectService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private PostCollectService postCollectService;

    @Value("${favorites.favoritesMaxNum}")
    private Long favoritesMaxNum;

    @RequestMapping("/create")
    public ResponseResult<Favorites> create(@Valid @RequestBody Favorites input) {
        LambdaQueryWrapper<Favorites> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = SecurityUtils.getUserId();
        queryWrapper.eq(Favorites::getUserId, userId);
        long count = favoritesService.count(queryWrapper);
        if (count >= favoritesMaxNum) {
            throw new SystemException(ResultCode.OTHER_ERROR.getCode(), "专栏数目已达上限" + favoritesMaxNum + "个");
        }
        input.setUserId(userId);
        boolean save = favoritesService.save(input);
        if (save) {
            return ResponseResult.success(input);
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @RequestMapping("/update")
    public ResponseResult<Favorites> update(@RequestBody Favorites input) {
        Favorites favorites = favoritesService.getById(input.getId());
        SecurityUtils.authAuthorizationUser(favorites.getUserId());
        boolean update = favoritesService.updateById(input);
        if (update) {
            return ResponseResult.success(input);
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @RequestMapping("/delete")
    public ResponseResult<Boolean> delete(@RequestParam Long id) {
        Favorites favorites = favoritesService.getById(id);
        SecurityUtils.authAuthorizationUser(favorites.getUserId());
        boolean remove = favoritesService.removeById(id);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/open/list")
    public ResponseResult<List<FavoritesListOutput>> list(Long userId) {
        Long authUserId = SecurityUtils.getUserId();
        if (null == userId) {
            userId = authUserId;
        }
        // 查询是否开放展示
        checkFavoritesShow(userId);

        LambdaQueryWrapper<Favorites> favoritesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        favoritesLambdaQueryWrapper.eq(Favorites::getUserId, userId);
        List<Favorites> favoritesList = favoritesService.list(favoritesLambdaQueryWrapper);

        if (favoritesList.isEmpty() && SecurityUtils.isContextUser(userId)) { // 如果没有收藏夹，并且是作者自己访问，创建一个默认的
            Favorites defaultFavorites = new Favorites();
            defaultFavorites.setName("默认收藏夹");
            defaultFavorites.setUserId(authUserId);
            defaultFavorites.setCover("https://youyu-source.oss-cn-beijing.aliyuncs.com/firstImages/default/defaultFirstPic.png");
            favoritesService.save(defaultFavorites);
            favoritesList.add(defaultFavorites);
        }

        // 构造输出
        List<FavoritesListOutput> resultList = BeanCopyUtils.copyBeanList(favoritesList, FavoritesListOutput.class);
        resultList.forEach(this::setEnhanceData);

        return ResponseResult.success(resultList);
    }

    /**
     * 设置收藏夹预览文章信息
     *
     * @param favorite 收藏夹
     */
    public void setEnhanceData(FavoritesListOutput favorite) {
        // 设置预览文章信息
        List<Long> postIds = postCollectService.getPostIdsByFavoriteId(favorite.getId());
        List<PostListOutput> previewPosts = new ArrayList<>();
        if (!postIds.isEmpty()) {
            Post post = postMapper.selectById(postIds.get(0));
            previewPosts.add(BeanCopyUtils.copyBean(post, PostListOutput.class));
        }
        favorite.setPreviewPosts(previewPosts);

        LambdaQueryWrapper<PostCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostCollect::getFavoritesId, favorite.getId());
        long count = postCollectService.count(queryWrapper);
        favorite.setCount(count);
    }

    /**
     * 检查指定用户是否有权限查看收藏夹
     *
     * @param userId 指定用户id
     */
    public void checkFavoritesShow(Long userId) {
        Long authUserId = SecurityUtils.getUserId();
        ProfileMenu profileMenu = userServiceClient.getProfileMenu(userId).getData();
        if (profileMenu.getShowFavorites() != 1 && !Objects.equals(userId, authUserId)) { // 私密，且不是主人
            throw new SystemException(ResultCode.FORBIDDEN);
        }
    }

    @RequestMapping("/open/postPage")
    public ResponseResult<PageOutput<PostListOutput>> page(@Valid FavoritesPageInput input) {
        Favorites favorites = favoritesService.getById(input.getId());
        if (Objects.isNull(favorites)) {
            return ResponseResult.success(null);
        }
        // 查询是否开放展示
        checkFavoritesShow(favorites.getUserId());

        // 水平越权检测
        Integer open = favorites.getOpen();
        if (open == 0) { // 私有
            SecurityUtils.authAuthorizationUser(favorites.getUserId());
        }

        List<Long> postIds = postCollectService.getPostIdsByFavoriteId(input.getId());
        LambdaQueryWrapper<Post> postLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postLambdaQueryWrapper.in(Post::getId, postIds);

        Page<Post> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<Post> postPage = postMapper.selectPage(page, postLambdaQueryWrapper);

        // 封装查询结果
        PageOutput<PostListOutput> pageOutput = PageUtils.setPageResult(postPage, PostListOutput.class);
        return ResponseResult.success(pageOutput);
    }
}

