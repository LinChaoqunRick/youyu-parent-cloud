package com.youyu.controller.post;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youyu.annotation.Log;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.*;
import com.youyu.entity.post.Post;
import com.youyu.entity.post.PostCollect;
import com.youyu.entity.post.PostLike;
import com.youyu.enums.LogType;
import com.youyu.enums.post.CreateType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.post.FavoritesService;
import com.youyu.service.post.PostCollectService;
import com.youyu.service.post.PostLikeService;
import com.youyu.service.post.PostService;
import com.youyu.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;

/**
 * (Post)表控制层
 *
 * @author makejava
 * @since 2023-01-01 16:28:22
 */
@RestController
@RequestMapping("/post")
public class PostController {
    @Resource
    private PostService postService;

    @Resource
    private PostLikeService postLikeService;

    @Resource
    private PostCollectService postCollectService;

    @Resource
    private FavoritesService favoritesService;

    @RequestMapping("/open/getUserDetailById")
    ResponseResult<PostUserOutput> getUserById(Long userId) {
        PostUserOutput user = postService.getUserDetailById(userId, true);
        return ResponseResult.success(user);
    }

    @RequestMapping("/open/list")
    public ResponseResult<PageOutput<PostListOutput>> list(PostListInput input) {
        PageOutput<PostListOutput> pageOutput = postService.postList(input);
        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/open/get")
    public ResponseResult<PostDetailOutput> get(@RequestParam Long postId) {
        PostDetailOutput post = postService.get(postId);
        post.setUser(postService.getUserDetailById(post.getUserId(), false));
        return ResponseResult.success(post);
    }

    @RequestMapping("/edit/detail")
    public ResponseResult<Post> editDetail(@NotNull @RequestParam Long postId) {
        Post post = postService.get(postId);
        Long authorId = SecurityUtils.getUserId();
        if (!authorId.equals(post.getUserId())) {
            throw new SystemException(ResultCode.CONTENT_NOT_EXIST);
        }
        return ResponseResult.success(post);
    }

    @RequestMapping("/create")
    @Log(title = "新增文章", type = LogType.INSERT)
    public ResponseResult<Long> create(@Valid @RequestBody Post post) {
        boolean save = postService.save(post);
        return ResponseResult.success(post.getId());
    }

    @RequestMapping("/update")
    @Log(title = "编辑文章", type = LogType.UPDATE)
    public ResponseResult<Boolean> update(@Valid @RequestBody Post post) {
        Long userId = SecurityUtils.getUserId();
        if (!userId.equals(post.getUserId())) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
        post.setUpdateTime(new Date());
        boolean save = postService.updateById(post);
        return ResponseResult.success(save);
    }

    @RequestMapping("/hide")
    @Log(title = "隐藏文章", type = LogType.UPDATE)
    public ResponseResult<Boolean> hide(@RequestParam Long postId, @RequestParam Integer status) {
        Post post = postService.get(postId);
        if (!Objects.equals(SecurityUtils.getUserId(), post.getUserId())) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
        LambdaUpdateWrapper<Post> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Post::getId, postId);
        updateWrapper.set(Post::getStatus, status);
        boolean hide = postService.update(updateWrapper);
        return ResponseResult.success(hide);
    }

    @RequestMapping("/delete")
    @Log(title = "删除文章", type = LogType.DELETE)
    public ResponseResult<Boolean> delete(@RequestParam Long postId) {
        Post post = postService.getById(postId);
        Long currentUserId = SecurityUtils.getUserId();
        if (!currentUserId.equals(post.getUserId())) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
        boolean remove = postService.removeById(postId);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/setPostLike")
    // @PreAuthorize("hasAuthority('test')")
    @Log(title = "点赞文章", type = LogType.INSERT)
    public ResponseResult<Boolean> setPostLike(@Valid PostLike postLike) {
        boolean result = postLikeService.save(postLike);
        return ResponseResult.success(result);
    }

    @RequestMapping("/isPostLike")
    public ResponseResult<Long> isPostLike(@Valid PostLike postLike) {
        LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostLike::getPostId, postLike.getPostId());
        queryWrapper.eq(PostLike::getUserId, postLike.getUserId());
        Long count = postLikeService.count(queryWrapper);
        return ResponseResult.success(count);
    }

    @RequestMapping("/cancelPostLike")
    @Log(title = "取消点赞文章", type = LogType.DELETE)
    public ResponseResult<Boolean> cancelPostLike(@Valid PostLike postLike) {
        LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostLike::getPostId, postLike.getPostId());
        queryWrapper.eq(PostLike::getUserId, postLike.getUserId());
        boolean remove = postLikeService.remove(queryWrapper);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/open/getCreateTypes")
    public ResponseResult<List<HashMap<String, String>>> getCreateTypes() {
        List<HashMap<String, String>> list = new ArrayList<>();
        for (CreateType type : EnumSet.allOf(CreateType.class)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("code", type.getCode());
            map.put("desc", type.getDesc());
            list.add(map);
        }
        return ResponseResult.success(list);
    }

    @RequestMapping("/postCollect")
    @Log(title = "收藏文章", type = LogType.INSERT)
    public ResponseResult<Boolean> postCollect(@Valid PostCollect postCollect) {
        Long userId = SecurityUtils.getUserId();
        postCollect.setUserId(userId);

        LambdaQueryWrapper<PostCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(PostCollect::getPostId, postCollect.getPostId())
                .eq(PostCollect::getUserId, postCollect.getUserId());
        PostCollect collect = postCollectService.getOne(queryWrapper);
        boolean result;
        // 收藏夹水平越权检测，只有收藏夹的主人才能添加到此收藏
        if (Objects.nonNull(postCollect.getFavoritesId())) {
            long ownerId = favoritesService.getFavoriteUserId(postCollect.getFavoritesId());
            SecurityUtils.authAuthorizationUser(ownerId);
            if (Objects.nonNull(collect)) { // 已经收藏了，那就更新
                collect.setFavoritesId(postCollect.getFavoritesId());
                result = postCollectService.updateById(collect);
            } else {
                result = postCollectService.save(postCollect);
            }
        } else { // 如果是取消收藏，水平越权检测，只有收藏人才能取消收藏
            if (Objects.nonNull(collect)) {  // 已经收藏了，那就删除
                SecurityUtils.authAuthorizationUser(collect.getUserId());
                result = postCollectService.removeById(collect.getId());
            } else {
                throw new SystemException(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), "收藏夹Id不能为空");
            }
        }
        return ResponseResult.success(result);
    }

    @RequestMapping("/getPostCollectInfo")
    public ResponseResult<PostCollect> isPostCollect(@RequestParam Long postId, @RequestParam Long userId) {
        LambdaQueryWrapper<PostCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostCollect::getPostId, postId);
        queryWrapper.eq(PostCollect::getUserId, userId);
        PostCollect collect = postCollectService.getOne(queryWrapper);
        return ResponseResult.success(collect);
    }

    @RequestMapping("/cancelPostCollect")
    @Log(title = "取消收藏文章", type = LogType.DELETE)
    public ResponseResult<Boolean> cancelPostCollect(@Valid PostCollect postCollect) {
        SecurityUtils.authAuthorizationUser(postCollect.getUserId());

        LambdaQueryWrapper<PostCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostCollect::getPostId, postCollect.getPostId())
                .eq(PostCollect::getUserId, postCollect.getUserId())
                .eq(PostCollect::getFavoritesId, postCollect.getFavoritesId());

        boolean remove = postCollectService.remove(queryWrapper);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/collectList")
    public ResponseResult<PageOutput<PostListOutput>> collectList(PostCollectListInput input) {
        PageOutput<PostListOutput> collectList = postCollectService.getCollectList(input);
        return ResponseResult.success(collectList);
    }

    @RequestMapping("/open/getLimitPost")
    ResponseResult<List<Post>> getFivePosts(@RequestParam(name = "userId") Long userId,
                                            @RequestParam(name = "count", defaultValue = "10") int count,
                                            @RequestParam(name = "orderBy", defaultValue = "create_time") String orderBy,
                                            @RequestParam(name = "orderType", defaultValue = "asc") String orderType) {

        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "title", "user_id", "view_count", "create_time", "create_type");
        queryWrapper.eq("user_id", userId);
        if (orderType.equals("desc")) {
            queryWrapper.orderByDesc(orderBy);
        } else {
            queryWrapper.orderByAsc(orderBy);
        }
        queryWrapper.last("limit " + count);
        List<Post> list = postService.list(queryWrapper);
        return ResponseResult.success(list);
    }

    @RequestMapping("/open/postListByIds")
    ResponseResult<List<PostListOutput>> postListByIds(@RequestParam List<Long> postIds) {
        List<PostListOutput> postListOutputs = postService.postListByIds(postIds);
        return ResponseResult.success(postListOutputs);
    }
}

