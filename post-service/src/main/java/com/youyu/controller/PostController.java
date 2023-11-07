package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.*;
import com.youyu.entity.Post;
import com.youyu.entity.PostCollect;
import com.youyu.entity.PostLike;
import com.youyu.entity.User;
import com.youyu.enums.CreateType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.PostCollectService;
import com.youyu.service.PostLikeService;
import com.youyu.service.PostService;
import com.youyu.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    @Autowired
    private PostService postService;

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private PostCollectService postCollectService;

    @RequestMapping("/getUserDetailById")
    ResponseResult<PostUserOutput> getUserById(Long userId) {
        PostUserOutput user = postService.getUserDetailById(userId, true);
        return ResponseResult.success(user);
    }

    @RequestMapping("/list")
    public ResponseResult<PageOutput<PostListOutput>> list(PostListInput input) {
        PageOutput<PostListOutput> pageOutput = postService.postList(input);
        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/get")
    public ResponseResult<PostDetailOutput> get(@RequestParam Long postId) {
        PostDetailOutput post = postService.get(postId);
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
    public ResponseResult<Long> create(@Valid @RequestBody Post post) {
        boolean save = postService.save(post);
        return ResponseResult.success(post.getId());
    }

    @RequestMapping("/update")
    public ResponseResult<Boolean> update(@Valid @RequestBody Post post) {
        Long userId = SecurityUtils.getUserId();
        if (!userId.equals(post.getUserId())) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
        post.setUpdateTime(new Date());
        boolean save = postService.updateById(post);
        return ResponseResult.success(save);
    }

    @RequestMapping("/delete")
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
    public ResponseResult<Boolean> setPostLike(@Valid PostLike postLike) {
        boolean result = postLikeService.save(postLike);
        return ResponseResult.success(result);
    }

    @RequestMapping("/isPostLike")
    public ResponseResult<Integer> isPostLike(@Valid PostLike postLike) {
        LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostLike::getPostId, postLike.getPostId());
        queryWrapper.eq(PostLike::getUserId, postLike.getUserId());
        int count = postLikeService.count(queryWrapper);
        return ResponseResult.success(count);
    }

    @RequestMapping("/cancelPostLike")
    public ResponseResult<Boolean> cancelPostLike(@Valid PostLike postLike) {
        LambdaQueryWrapper<PostLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostLike::getPostId, postLike.getPostId());
        queryWrapper.eq(PostLike::getUserId, postLike.getUserId());
        boolean remove = postLikeService.remove(queryWrapper);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/getCreateTypes")
    public ResponseResult<List<HashMap<String, String>>> getCreateTypes() {
        List<HashMap<String, String>> list = new ArrayList<>();
        for (CreateType type : EnumSet.allOf(CreateType.class)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("code", type.getCode() + "");
            map.put("desc", type.getDesc());
            list.add(map);
        }
        return ResponseResult.success(list);
    }

    @RequestMapping("/postCollect")
    public ResponseResult<Boolean> postCollect(@Valid PostCollect postCollect) {
        boolean result = postCollectService.save(postCollect);
        return ResponseResult.success(result);
    }

    @RequestMapping("/isPostCollect")
    public ResponseResult<Integer> isPostCollect(@Valid PostCollect postCollect) {
        LambdaQueryWrapper<PostCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostCollect::getPostId, postCollect.getPostId());
        queryWrapper.eq(PostCollect::getUserId, postCollect.getUserId());
        int count = postCollectService.count(queryWrapper);
        return ResponseResult.success(count);
    }

    @RequestMapping("/cancelPostCollect")
    public ResponseResult<Boolean> cancelPostCollect(@Valid PostCollect postCollect) {
        LambdaQueryWrapper<PostCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostCollect::getPostId, postCollect.getPostId());
        queryWrapper.eq(PostCollect::getUserId, postCollect.getUserId());
        boolean remove = postCollectService.remove(queryWrapper);
        return ResponseResult.success(remove);
    }

    @RequestMapping("/collectList")
    public ResponseResult<PageOutput<PostListOutput>> collectList(PostCollectListInput input) {
        PageOutput<PostListOutput> collectList = postCollectService.getCollectList(input);
        return ResponseResult.success(collectList);
    }

    @RequestMapping("/getLimitPost")
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
}

