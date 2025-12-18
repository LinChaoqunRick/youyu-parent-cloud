package com.youyu.service.post.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.Actor;
import com.youyu.dto.ActorBase;
import com.youyu.dto.comment.CommentListInput;
import com.youyu.dto.comment.CommentListOutput;
import com.youyu.dto.comment.ReplyCountDTO;
import com.youyu.dto.mail.CommentMailSendInput;
import com.youyu.dto.page.PageOutput;
import com.youyu.entity.post.Comment;
import com.youyu.entity.post.CommentLike;
import com.youyu.entity.post.Post;
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
import com.youyu.utils.PageUtils;

import java.util.*;
import java.util.stream.Collectors;

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

        // 1. 查询基础分页数据（使用安全的 orderBy）
        Page<Comment> page = queryCommentPage(input);
        PageOutput<CommentListOutput> pageOutput = PageUtils.setPageResult(page, CommentListOutput.class);

        if (pageOutput.getList().isEmpty()) {
            return pageOutput;
        }

        // 收集所有需要的ID
        List<Long> commentIds = pageOutput.getList().stream().map(CommentListOutput::getId).collect(Collectors.toList());
        List<Long> rootCommentIds = input.getRootId() == null
            ? commentIds
            : Collections.emptyList();
        Set<Long> replyIds = pageOutput.getList().stream()
            .map(CommentListOutput::getReplyId)
            .filter(replyId -> replyId != -1)
            .collect(Collectors.toSet());

        // 2. 批量查询点赞状态
        Set<Long> likedCommentIds = userId != null
            ? commentLikeService.getLikedCommentIds(userId, commentIds)
            : Collections.emptySet();

        // 3. 批量查询回复数量（仅对根评论）
        Map<Long, Long> replyCountMap = new HashMap<>();
        if (!rootCommentIds.isEmpty()) {
            List<ReplyCountDTO> replyCountList = commentMapper.batchGetReplyCount(rootCommentIds);
            replyCountMap = replyCountList.stream()
                .collect(Collectors.toMap(ReplyCountDTO::getRootId, ReplyCountDTO::getReplyCount));
        }

        // 4. 批量查询子评论（仅对根评论）
        Map<Long, List<Comment>> repliesMap = new HashMap<>();
        if (!rootCommentIds.isEmpty()) {
            List<Comment> allReplies = commentMapper.batchGetLatestReplies(rootCommentIds, 2);
            repliesMap = allReplies.stream().collect(Collectors.groupingBy(Comment::getRootId));
        }

        // 5. 批量查询被回复的评论
        Map<Long, Comment> repliedCommentMap = replyIds.isEmpty()
            ? Collections.emptyMap()
            : commentMapper.selectBatchIds(replyIds).stream()
                .collect(Collectors.toMap(Comment::getId, c -> c));

        // 6. 收集所有actor信息
        List<ActorBase> actorBases = new ArrayList<>(pageOutput.getList().stream()
            .map(this::getCommentActor).collect(Collectors.toList()));
        Map<Long, ActorBase> tempReplyActorMap = new HashMap<>();

        // 7. 填充数据
        Map<Long, Long> finalReplyCountMap = replyCountMap;
        Map<Long, List<Comment>> finalRepliesMap = repliesMap;
        pageOutput.getList().forEach(item -> {
            // 设置地址名称
            item.setAdname(LocateUtils.getShortNameByCode(String.valueOf(item.getAdcode())));

            // 设置点赞状态
            item.setCommentLike(likedCommentIds.contains(item.getId()));

            // 处理根评论的回复信息
            if (input.getRootId() == null) {
                Long replyCount = finalReplyCountMap.getOrDefault(item.getId(), 0L);
                item.setReplyCount(replyCount);

                if (replyCount > 0) {
                    List<Comment> replies = finalRepliesMap.getOrDefault(item.getId(), Collections.emptyList());
                    actorBases.addAll(replies.stream().map(this::getCommentActor).collect(Collectors.toList()));

                    List<CommentListOutput> children = BeanCopyUtils.copyBeanList(replies, CommentListOutput.class);
                    children.forEach(child -> {
                        child.setAdname(LocateUtils.getShortNameByCode(String.valueOf(child.getAdcode())));
                        if (child.getReplyId() > -1) {
                            tempReplyActorMap.put(child.getReplyId(), getCommentActor(child));
                        }
                    });
                    item.setChildren(children);
                }
            }

            // 处理被回复评论的actor信息
            if (item.getReplyId() != -1) {
                Comment repliedComment = repliedCommentMap.get(item.getReplyId());
                if (repliedComment != null) {
                    ActorBase repliedActorBase = getCommentActor(repliedComment);
                    actorBases.add(repliedActorBase);
                    tempReplyActorMap.put(repliedComment.getId(), repliedActorBase);
                }
            }
        });

        // 8. 批量查询actor信息并填充
        Map<Integer, Map<Long, Actor>> actorMap = actorService.makeActorMap(actorBases);
        pageOutput.getList().forEach(item -> {
            fillCommentActor(item, actorMap, tempReplyActorMap);
            if (item.getChildren() != null) {
                item.getChildren().forEach(child -> fillCommentActor(child, actorMap, tempReplyActorMap));
            }
        });

        return pageOutput;
    }

    /**
     * 查询评论分页数据
     */
    private Page<Comment> queryCommentPage(CommentListInput input) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();

        if (input.getRootId() != null) {
            // 子评论只需要rootId就可以查询
            queryWrapper.eq(Comment::getRootId, input.getRootId());
        } else {
            // 根评论需要postId
            queryWrapper.eq(Comment::getPostId, input.getPostId());
            queryWrapper.eq(Comment::getRootId, -1);
        }

        // 使用安全的 orderBy（已验证白名单）
        String validatedOrderBy = input.getValidatedOrderBy();
        queryWrapper.last("order by " + validatedOrderBy + " " + (input.isAsc() ? "asc" : "desc"));

        Page<Comment> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, queryWrapper);
        return page;
    }

    @Override
    public CommentListOutput createComment(@Valid Comment comment) {
        int insert = commentMapper.insert(comment);
        if (insert > 0) {
            CommentListOutput detail = getCommentById(comment.getId());

            Actor actor = detail.getActor();
            Actor actorTo = detail.getActorTo();
            Post post = postService.getById(detail.getPostId());
            String actorEmail = userServiceClient.getActorEmailById(actor.getId(), actor.getType()).getData();
            String actorToEmail = userServiceClient.getActorEmailById(actorTo.getId(), actorTo.getType()).getData();

            boolean isComment = detail.getRootId() == -1; // 是评论，而不是回复

            if (!actorEmail.equals(actorToEmail)) {
                // 如果不是自己，就发送邮件，是自己就不发
                CommentMailSendInput mailSendInput = new CommentMailSendInput();
                mailSendInput.setTo(actorToEmail);
                mailSendInput.setActorNickname(actor.getNickname());
                mailSendInput.setActorToNickname(actorTo.getNickname());
                mailSendInput.setContentType("文章");
                // 根据 isComment 设置不同的邮件标题
                if (isComment) {
                    // 评论：您的文章【xxx】有了新的评论
                    mailSendInput.setSubject("您的文章【" + StringUtils.ellipsisUnicode(post.getTitle(), 20) + "】有了新的评论");
                } else {
                    // 回复：您在【xxx】的评论有了新的回复
                    mailSendInput.setSubject("您在【" + StringUtils.ellipsisUnicode(post.getTitle(), 20) + "】的评论有了新的回复");
                }
                mailSendInput.setTitle(StringUtils.ellipsisUnicode(post.getTitle(), 20));
                mailSendInput.setCommentType(isComment ? "评论" : "回复");
                mailSendInput.setContent(detail.getContent());
                mailSendInput.setLink("https://v2.youyul.com");
                template.convertAndSend("amq.direct", "commentMail", mailSendInput);
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
            // 存在回复的id，说明不是根评论，而是子评论或回复了子评论
            Comment replyComment = commentMapper.selectById(replyId);
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
