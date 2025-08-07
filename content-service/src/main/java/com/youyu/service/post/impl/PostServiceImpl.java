package com.youyu.service.post.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.column.ColumnListOutput;
import com.youyu.dto.post.comment.CommentListOutput;
import com.youyu.dto.post.post.*;
import com.youyu.entity.post.Category;
import com.youyu.entity.post.Post;
import com.youyu.entity.post.PostCollect;
import com.youyu.entity.post.PostLike;
import com.youyu.entity.user.User;
import com.youyu.enums.post.CreateType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.post.CommentMapper;
import com.youyu.mapper.post.PostCollectMapper;
import com.youyu.mapper.post.PostLikeMapper;
import com.youyu.mapper.post.PostMapper;

import com.youyu.service.post.CategoryService;
import com.youyu.service.post.ColumnService;
import com.youyu.service.post.PostService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * (Post)表服务实现类
 *
 * @author makejava
 * @since 2023-01-01 16:28:29
 */
@Service("postService")
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private CategoryService categoryService;

    @Resource
    private PostMapper postMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private PostLikeMapper postLikeMapper;

    @Resource
    private PostCollectMapper postCollectMapper;

    @Resource
    private ColumnService columnService;

    @Override
    public PageOutput<PostListOutput> postList(PostListInput input) {
        // 查询条件
        LambdaQueryWrapper<Post> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(Post.class, item -> !item.getColumn().equals("content"));
        // 如果 有categoryId 就要求 查询时要和传入的相同
        lambdaQueryWrapper.eq(Objects.nonNull(input.getCategoryId()), Post::getCategoryId, input.getCategoryId());
        // 如果 有userId 就要求 查询时要和传入的相同
        lambdaQueryWrapper.eq(Objects.nonNull(input.getUserId()), Post::getUserId, input.getUserId());
        // 如果 有isOriginal 就要求 查询时要和传入的相同
        lambdaQueryWrapper.eq(input.isOriginal(), Post::getCreateType, 0);
        // 状态是正式发布的
        if (Objects.isNull(SecurityUtils.getUserId()) || !Objects.equals(SecurityUtils.getUserId(), input.getUserId())) {
            lambdaQueryWrapper.eq(Post::getStatus, 0);
        }
        // 对isTop进行降序
        // lambdaQueryWrapper.orderByDesc(Post::getIsTop);
        // lambdaQueryWrapper.orderByDesc(Post::getCreateTime);
        // 模糊查询
        lambdaQueryWrapper
                .like(Objects.nonNull(input.getKey()), Post::getTitle, input.getKey())
                .or()
                .like(Objects.nonNull(input.getKey()), Post::getSummary, input.getKey());
        // 如果 有sort 就要求 查询时要和传入的相同
        if (input.isSort()) {
            lambdaQueryWrapper.orderByDesc(Post::getViewCount);
        } else {
            lambdaQueryWrapper.orderByDesc(Post::getCreateTime);
        }

        // 分页查询
        Page<Post> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, lambdaQueryWrapper);

        // 封装查询结果
        PageOutput<PostListOutput> pageOutput = PageUtils.setPageResult(page, PostListOutput.class);

        // 查询categoryName, 普通循环
        pageOutput.getList().forEach(this::setPostListData);

        // 查询categoryName, stream流

        return pageOutput;
    }

    @Override
    public List<PostListOutput> postListByIds(List<Long> ids) {
        if (Objects.isNull(ids)) {
            return null;
        }
        LambdaQueryWrapper<Post> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Post::getId, ids);
        List<Post> postList = postMapper.selectList(lambdaQueryWrapper);
        List<PostListOutput> outputs = BeanCopyUtils.copyBeanList(postList, PostListOutput.class);
        outputs.forEach(this::setPostListData);
        return outputs;
    }

    @Override
    public PostDetailOutput get(Long postId) {
        Post post = postMapper.selectById(postId);

        if (post.getStatus() == 1 && !Objects.equals(post.getUserId(), SecurityUtils.getUserId())) {
            throw new SystemException(ResultCode.FORBIDDEN);
        }

        if (Objects.isNull(post)) {
            throw new SystemException(ResultCode.NOT_FOUND);
        }
        PostDetailOutput postDetail = BeanCopyUtils.copyBean(post, PostDetailOutput.class);

        if (postDetail.getCategoryId() != null) {
            postDetail.setCategoryName(categoryService.getById(postDetail.getCategoryId()).getName());
        }
        if (postDetail.getCreateType() != null) {
            postDetail.setCreateTypeDesc(CreateType.getDescByCode(postDetail.getCreateType()));
        }

        // 查询评论数量
        Long commentCount = getCommentCount(postId);
        postDetail.setCommentCount(commentCount);

        // 查询点赞数量
        Long likeCount = getLikeCount(postId);
        postDetail.setLikeCount(likeCount);

        // 查询收藏数量
        Long collectCount = getCollectCount(postId);
        postDetail.setCollectCount(collectCount);

        // 查询专栏信息
        if (Objects.nonNull(post.getColumnIds())) {
            List<ColumnListOutput> columnList = columnService.getColumnListByIds(post.getColumnIds().split(","));
            postDetail.setColumns(columnList);
        }

        // 当前登录用户的id
        Long userId = SecurityUtils.getUserId();
        if (!Objects.isNull(userId)) {
            //查询是否点赞
            LambdaQueryWrapper<PostLike> likeQueryWrapper = new LambdaQueryWrapper<>();
            likeQueryWrapper.eq(PostLike::getPostId, postDetail.getId());
            likeQueryWrapper.eq(PostLike::getUserId, userId);
            Long count = postLikeMapper.selectCount(likeQueryWrapper);
            if (count > 0) {
                postDetail.setPostLike(true);
            }

            //查询是否收藏
            LambdaQueryWrapper<PostCollect> collectQueryWrapper = new LambdaQueryWrapper<>();
            collectQueryWrapper.eq(PostCollect::getPostId, postDetail.getId());
            collectQueryWrapper.eq(PostCollect::getUserId, userId);
            Long count2 = postCollectMapper.selectCount(collectQueryWrapper);
            if (count2 > 0) {
                postDetail.setPostCollect(true);
            }
        }

        // 浏览量+1
        post.setViewCount(post.getViewCount() + 1);
        postMapper.updateById(post);

        return postDetail;
    }

    @Override
    public Long getCommentCount(Long postId) {
        int commentCount = 0;
        List<CommentListOutput> commentList = commentMapper.getCommentCountByPostId(postId);
        commentCount += commentList.size();
        Long subRepliesCount = commentList.stream()
                .mapToLong(CommentListOutput::getReplyCount)
                .sum();
        commentCount += subRepliesCount;
        return (long) commentCount;
    }

    @Override
    public Long getLikeCount(Long postId) {
        LambdaQueryWrapper<PostLike> postLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postLikeLambdaQueryWrapper.eq(PostLike::getPostId, postId);
        return postLikeMapper.selectCount(postLikeLambdaQueryWrapper);
    }

    @Override
    public Long getCollectCount(Long postId) {
        LambdaQueryWrapper<PostCollect> postLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        postLikeLambdaQueryWrapper.eq(PostCollect::getPostId, postId);
        return postCollectMapper.selectCount(postLikeLambdaQueryWrapper);
    }

    @Override
    public PostUserOutput getUserDetailById(Long userId, boolean enhance) {
        if (Objects.isNull(userId)) {
            return null;
        }
        User user = userServiceClient.selectById(userId).getData();
        if (!Objects.isNull(user)) {
            PostUserOutput userDetailOutput = BeanCopyUtils.copyBean(user, PostUserOutput.class);
            if (enhance) {
                LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Post::getUserId, userId);
                queryWrapper.select(Post::getViewCount, Post::getCreateType);
                List<Post> list = postMapper.selectList(queryWrapper);
                PostUserExtraInfo extraInfo = new PostUserExtraInfo(0L, 0L, 0L, 0L);
                // 原创文章数量、阅读量
                list.stream()
                        .filter(post -> post.getCreateType().equals("0"))
                        .forEach(item -> {
                            extraInfo.setPostCount(extraInfo.getPostCount() + 1);
                            extraInfo.setViewCount(extraInfo.getViewCount() + item.getViewCount());
                        });

                // 被点赞数量
                LambdaQueryWrapper<PostLike> postLikeQueryWrapper = new LambdaQueryWrapper<>();
                postLikeQueryWrapper.eq(PostLike::getUserIdTo, userId);
                long likeCount = postLikeMapper.selectCount(postLikeQueryWrapper);
                extraInfo.setLikeCount(likeCount);

                // 粉丝数量
                long fansCount = userServiceClient.getFansCount(userId).getData();
                extraInfo.setFansCount(fansCount);

                userDetailOutput.setExtraInfo(extraInfo);

                // 是否关注
                Long currentUserId = SecurityUtils.getUserId();
                if (!Objects.isNull(currentUserId)) {
                    int count = userServiceClient.isFollow(currentUserId, userId).getData();
                    userDetailOutput.setFollow(count > 0);
                }
            }
            return userDetailOutput;
        } else {
            return null;
        }
    }

    @Override
    public void setPostListData(PostListOutput post) {
        Category category = categoryService.getById(post.getCategoryId());
        if (!Objects.isNull(category)) {
            post.setCategoryName(category.getName());
        }

        // 查询评论数量
        Long commentCount = getCommentCount(post.getId());
        post.setCommentCount(commentCount);

        // 查询用户信息
        PostUserOutput user = getUserDetailById(post.getUserId(), false);
        post.setUser(user);
    }
}

