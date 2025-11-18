package com.youyu.service.post.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.dto.post.comment.CommentListInput;
import com.youyu.dto.post.comment.CommentListOutput;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.post.post.PostUserOutput;
import com.youyu.dto.user.ActorBase;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.moment.MomentUserOutput;
import com.youyu.entity.post.Comment;
import com.youyu.entity.post.CommentLike;
import com.youyu.entity.post.Post;
import com.youyu.entity.user.Actor;
import com.youyu.enums.ActorType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.post.CommentMapper;
import com.youyu.service.actor.ActorService;
import com.youyu.service.post.CommentLikeService;
import com.youyu.service.post.CommentService;
import com.youyu.service.post.PostService;
import com.youyu.utils.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.*;

/**
 * (Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-02-12 21:20:32
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private PostService postService;

    @Resource
    private CommentLikeService commentLikeService;

    @Resource
    private ActorService actorService;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    RabbitTemplate template;

    @Override
    public PageOutput<CommentListOutput> getCommentsPage(CommentListInput input) {
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        if (input.getRootId() != null) {
            // 子评论只需要rootId就可以查询
            queryWrapper.eq(Comment::getRootId, input.getRootId());
        } else {
            // 根评论需要postId
            queryWrapper.eq(Comment::getPostId, input.getPostId());
            queryWrapper.eq(Comment::getRootId, -1);
        }
        queryWrapper.last("order by" + " " + input.getOrderBy() + " " + (input.isAsc() ? "asc" : "desc"));
        // 分页查询
        Page<Comment> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, queryWrapper);
        // 封装查询结果
        PageOutput<CommentListOutput> pageOutput = PageUtils.setPageResult(page, CommentListOutput.class);
        // 收集所有actor信息，一次性查询
        List<ActorBase> actorBases = new ArrayList<>(pageOutput.getList().stream().map(this::getCommentActor).toList());
        // 仅临时记录被回复的评论
        Map<Long, ActorBase> tempReplyActorMap = new HashMap<>();
        // 处理额外信息
        pageOutput.getList().forEach(item -> {
            item.setAdname(LocateUtils.getShortNameByCode(String.valueOf(item.getAdcode())));
            // 查询是否点赞
            if (Objects.nonNull(userId)) {
                CommentLike commentLike = new CommentLike();
                commentLike.setCommentId(item.getId());
                item.setCommentLike(commentLikeService.isPostCommentLike(commentLike));
            }
            // 根评论，查询回复数量，最早n条回复
            if (input.getRootId() == null) {
                LambdaQueryWrapper<Comment> replyQueryWrapper = new LambdaQueryWrapper<>();
                replyQueryWrapper.eq(Comment::getRootId, item.getId());
                replyQueryWrapper.orderByDesc(Comment::getCreateTime);
                Long replyCount = commentMapper.selectCount(replyQueryWrapper);
                item.setReplyCount(replyCount);
                if (replyCount > 0) {
                    List<Comment> repliesList = commentMapper.selectList(replyQueryWrapper.last("LIMIT 2"));
                    actorBases.addAll(repliesList.stream().map(this::getCommentActor).toList());
                    List<CommentListOutput> children = BeanCopyUtils.copyBeanList(repliesList, CommentListOutput.class);
                    children.forEach(child -> {
                        child.setAdname(LocateUtils.getShortNameByCode(String.valueOf(child.getAdcode())));
                        if (child.getReplyId() > -1) {
                            tempReplyActorMap.put(child.getReplyId(), getCommentActor(child));
                        }
                    });
                    item.setChildren(children);
                }
            }
            // 子评论，如果回复是回复了某条子评论，查询被回复人信息
            if (item.getReplyId() != -1) {
                // 如果回复了某条评论，就把被回复人的信息查询出来
                Comment repliedComment = commentMapper.selectById(item.getReplyId());
                ActorBase repliedActorBase = getCommentActor(repliedComment);
                actorBases.add(repliedActorBase);
                tempReplyActorMap.put(repliedComment.getId(), repliedActorBase);
            }
        });
        // 批量查询actor信息
        Map<Integer, Map<Long, Actor>> actorMap = actorService.makeActorMap(actorBases);
        // 填充actor信息
        pageOutput.getList().forEach(item -> {
            fillCommentActor(item, actorMap, tempReplyActorMap);
            if (item.getChildren() != null) {
                item.getChildren().forEach(child -> fillCommentActor(child, actorMap, tempReplyActorMap));
            }
        });

        return pageOutput;
    }

    @Override
    public CommentListOutput createComment(@Valid Comment comment) {
        int insert = commentMapper.insert(comment);
        if (insert > 0) {
            CommentListOutput detail = getCommentById(comment.getId());
            // 如果回复的是自己，不发送邮件
            if (!detail.getActor().getId().equals(detail.getActorTo().getId())) {
                template.convertAndSend("amq.direct", "postCommentMail", detail);
            }
            return detail;
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public CommentListOutput getCommentById(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        CommentListOutput output = BeanCopyUtils.copyBean(comment, CommentListOutput.class);
        // 评论人信息查询
        ActorBase actorBase = getCommentActor(comment);
        Actor actor = userServiceClient.getActorById(actorBase.getActorId(), actorBase.getActorType()).getData();
        output.setActor(actor);
        // 被评论人信息查询
        Long replyId = getCommentReplyId(comment);
        if (replyId != null) {
            // 不是根评论，而是子评论或回复了子评论
            Comment replyComment = comment.selectById(replyId);
            ActorBase actorBaseTo = getCommentActor(replyComment);
            if (Objects.nonNull(actorBaseTo.getActorId())) {
                Actor actorTo = userServiceClient.getActorById(actorBaseTo.getActorId(), actorBaseTo.getActorType()).getData();
                output.setActorTo(actorTo);
            }
        } else {
            // 是根评论，就要查询时刻的发布者
            Post post = postService.getById(comment.getPostId());
            Actor actorTo = userServiceClient.getActorById(post.getUserId(), ActorType.USER.getCode()).getData();
            output.setActorTo(actorTo);
        }
        return output;
    }

    @Override
    public Boolean deleteComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        Post post = postService.getById(comment.getPostId());
        Long authorId = post.getUserId();
        Long currentUserId = SecurityUtils.getUserId();
        Long commentUserId = comment.getUserId();
        if (commentUserId.equals(currentUserId) || authorId.equals(currentUserId)) { // 仅自己和文章的作者可删除
            int delete = commentMapper.deleteById(commentId);
            if (delete > 0) {
                return true;
            } else {
                throw new SystemException(ResultCode.OPERATION_FAIL);
            }
        } else {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
    }

    /**
     * 获取评论的actor信息
     * 如果userId存在，说明是来自用户的评论，如果visitorId存在，说明是游客的评论
     *
     * @param comment 文章评论
     * @return ActorBase
     */
    public ActorBase getCommentActor(Comment comment) {
        ActorBase input = new ActorBase();
        if (comment.getUserId() != null && comment.getUserId() != -1) {
            input.setActorId(comment.getUserId());
            input.setActorType(0);
        } else {
            input.setActorId(comment.getVisitorId());
            input.setActorType(1);
        }
        return input;
    }

    /**
     * 填充 comment 的 actor 信息, 根据 actorId + actorType 从 actorMap 中查询 actor 信息
     *
     * @param comment         评论
     * @param actorMap        actorMap 包含用户和游客两个map
     * @param repliedActorMap 被回复的评论的map
     */
    public void fillCommentActor(CommentListOutput comment, Map<Integer, Map<Long, Actor>> actorMap, Map<Long, ActorBase> repliedActorMap) {
        ActorBase topActorBase = getCommentActor(comment);
        comment.setActor(ActorUtils.getActorWithMap(topActorBase.getActorId(), topActorBase.getActorType(), actorMap));
        if (comment.getReplyId() != -1) {
            // 如果回复了某条评论，就把被回复人的信息查询出来
            ActorBase repliedActorBase = repliedActorMap.get(comment.getReplyId());
            comment.setActorTo(ActorUtils.getActorWithMap(repliedActorBase.getActorId(), repliedActorBase.getActorType(), actorMap));
        }
    }

    /**
     * 获取评论的子评论或跟评论的id
     * 如果回复的是子评论，那么优先查询被回复的子评论信息，如果回复的是根评论，那么就查询被回复的根评论的信息
     *
     * @param comment 评论
     * @return 被回复的评论的id
     */
    public Long getCommentReplyId(Comment comment) {
        return comment.getReplyId() > -1 ? comment.getReplyId() : comment.getRootId() > -1 ? comment.getRootId() : null;
    }
}
