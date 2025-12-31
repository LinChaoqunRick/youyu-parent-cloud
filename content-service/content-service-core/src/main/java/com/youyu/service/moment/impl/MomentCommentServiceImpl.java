package com.youyu.service.moment.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.Actor;
import com.youyu.dto.ActorBase;
import com.youyu.dto.mail.CommentMailSendInput;
import com.youyu.dto.page.PageOutput;
import com.youyu.dto.moment.MomentCommentListInput;
import com.youyu.dto.moment.MomentCommentOutput;
import com.youyu.dto.moment.ReplyCountDTO;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.moment.MomentCommentLike;
import com.youyu.enums.ActorType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.moment.MomentCommentMapper;
import com.youyu.service.actor.ActorService;
import com.youyu.service.moment.MomentCommentLikeService;
import com.youyu.service.moment.MomentCommentService;
import com.youyu.service.moment.MomentService;
import com.youyu.utils.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import com.youyu.utils.PageUtils;

import java.util.*;
import java.util.stream.Collectors;
import com.youyu.dto.moment.MomentCommentCountDTO;

/**
 * (MomentComment)表服务实现类
 *
 * @author makejava
 * @since 2023-06-18 20:28:05
 */
@Service("momentCommentService")
public class MomentCommentServiceImpl extends ServiceImpl<MomentCommentMapper, MomentComment> implements MomentCommentService {

    @Resource
    private MomentCommentMapper momentCommentMapper;

    @Resource
    private MomentService momentService;

    @Resource
    private MomentCommentLikeService momentCommentLikeService;

    @Resource
    private ActorService actorService;

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    RabbitTemplate template;

    @Override
    public MomentCommentOutput createComment(MomentComment input) {
        int save = momentCommentMapper.insert(input);
        if (save > 0) {
            MomentCommentOutput detail = getCommentById(input.getId());
            Actor actor = detail.getActor();
            Actor actorTo = detail.getActorTo();
            Moment moment = momentService.getById(input.getMomentId());
            String actorEmail = userServiceClient.getActorEmailById(actor.getId(), actor.getType()).getData();
            String actorToEmail = userServiceClient.getActorEmailById(actorTo.getId(), actorTo.getType()).getData();

            boolean isComment = detail.getRootId() == -1; // 是评论，而不是回复

            if (!actorEmail.equals(actorToEmail)) {
                // 如果不是自己，就发送邮件，是自己就不发
                CommentMailSendInput mailSendInput = new CommentMailSendInput();
                mailSendInput.setTo(actorToEmail);
                mailSendInput.setActorNickname(actor.getNickname());
                mailSendInput.setActorToNickname(actorTo.getNickname());
                mailSendInput.setContentType("时刻");
                // 根据 isComment 设置不同的邮件标题
                if (isComment) {
                    // 评论：您的文章【xxx】有了新的评论
                    mailSendInput.setSubject("您的时刻【" + StringUtils.ellipsisUnicode(moment.getContent(), 20) + "】有了新的评论");
                } else {
                    // 回复：您在【xxx】的评论有了新的回复
                    mailSendInput.setSubject("您在【" + StringUtils.ellipsisUnicode(moment.getContent(), 20) + "】的评论有了新的回复");
                }
                mailSendInput.setTitle(StringUtils.ellipsisUnicode(moment.getContent(), 20));
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
    public PageOutput<MomentCommentOutput> momentCommentPage(MomentCommentListInput input) {
        Long userId = SecurityUtils.getUserId();

        // 1. 查询基础分页数据（使用安全的 orderBy）
        Page<MomentComment> page = queryCommentPage(input);
        PageOutput<MomentCommentOutput> pageOutput = PageUtils.setPageResult(page, MomentCommentOutput.class);

        if (pageOutput.getList().isEmpty()) {
            return pageOutput;
        }

        // 收集所有需要的ID
        List<Long> commentIds = pageOutput.getList().stream().map(MomentCommentOutput::getId).collect(Collectors.toList());
        List<Long> rootCommentIds = input.getRootId() == null
            ? commentIds
            : Collections.emptyList();
        Set<Long> replyIds = pageOutput.getList().stream()
            .map(MomentCommentOutput::getReplyId)
            .filter(replyId -> replyId != -1)
            .collect(Collectors.toSet());

        // 2. 批量查询点赞状态
        Set<Long> likedCommentIds = userId != null
            ? momentCommentLikeService.getLikedCommentIds(userId, commentIds)
            : Collections.emptySet();

        // 3. 批量查询回复数量（仅对根评论）
        Map<Long, Long> replyCountMap = new HashMap<>();
        if (!rootCommentIds.isEmpty()) {
            List<ReplyCountDTO> replyCountList = momentCommentMapper.batchGetReplyCount(rootCommentIds);
            replyCountMap = replyCountList.stream()
                .collect(Collectors.toMap(ReplyCountDTO::getRootId, ReplyCountDTO::getReplyCount));
        }

        // 4. 批量查询子评论（仅对根评论）
        Map<Long, List<MomentComment>> repliesMap = new HashMap<>();
        if (!rootCommentIds.isEmpty()) {
            List<MomentComment> allReplies = momentCommentMapper.batchGetLatestReplies(rootCommentIds, 2);
            repliesMap = allReplies.stream().collect(Collectors.groupingBy(MomentComment::getRootId));
        }

        // 5. 批量查询被回复的评论
        Map<Long, MomentComment> repliedCommentMap = replyIds.isEmpty()
            ? Collections.emptyMap()
            : momentCommentMapper.selectBatchIds(replyIds).stream()
                .collect(Collectors.toMap(MomentComment::getId, c -> c));

        // 6. 收集所有actor信息
        List<ActorBase> actorBases = new ArrayList<>(pageOutput.getList().stream()
            .map(ContentActorUtils::getCommentActor).collect(Collectors.toList()));
        Map<Long, ActorBase> tempReplyActorMap = new HashMap<>();

        // 7. 填充数据
        Map<Long, Long> finalReplyCountMap = replyCountMap;
        Map<Long, List<MomentComment>> finalRepliesMap = repliesMap;
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
                    List<MomentComment> replies = finalRepliesMap.getOrDefault(item.getId(), Collections.emptyList());
                    actorBases.addAll(replies.stream().map(ContentActorUtils::getCommentActor).collect(Collectors.toList()));

                    List<MomentCommentOutput> children = BeanCopyUtils.copyBeanList(replies, MomentCommentOutput.class);
                    children.forEach(child -> {
                        child.setAdname(LocateUtils.getShortNameByCode(String.valueOf(child.getAdcode())));
                        if (child.getReplyId() > -1) {
                            tempReplyActorMap.put(child.getReplyId(), ContentActorUtils.getCommentActor(child));
                        }
                    });
                    item.setChildren(children);
                }
            }

            // 处理被回复评论的actor信息
            if (item.getReplyId() != -1) {
                MomentComment repliedComment = repliedCommentMap.get(item.getReplyId());
                if (repliedComment != null) {
                    ActorBase repliedActorBase = ContentActorUtils.getCommentActor(repliedComment);
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
    private Page<MomentComment> queryCommentPage(MomentCommentListInput input) {
        LambdaQueryWrapper<MomentComment> queryWrapper = new LambdaQueryWrapper<>();

        // 子评论只需要rootId就可以查询
        if (input.getRootId() != null) {
            queryWrapper.eq(MomentComment::getRootId, input.getRootId());
        } else {
            queryWrapper.eq(MomentComment::getMomentId, input.getMomentId());
            queryWrapper.eq(MomentComment::getRootId, -1);
        }

        // 使用安全的 orderBy（已验证白名单）
        String validatedOrderBy = input.getValidatedOrderBy();
        queryWrapper.last("order by " + validatedOrderBy + " " + (input.isAsc() ? "asc" : "desc"));

        Page<MomentComment> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, queryWrapper);
        return page;
    }

    public MomentCommentOutput getCommentById(Long commentId) {
        MomentComment comment = momentCommentMapper.selectById(commentId);
        MomentCommentOutput output = BeanCopyUtils.copyBean(comment, MomentCommentOutput.class);
        // 评论人信息查询
        ActorBase actorBase = ContentActorUtils.getCommentActor(comment);
        Actor actor = userServiceClient.getActorById(actorBase.getActorId(), actorBase.getActorType()).getData();
        output.setActor(actor);
        // 被评论人信息查询
        Long replyId = getCommentReplyId(comment);
        if (replyId != null) {
            // 存在回复id，说明不是根评论，而是子评论或回复了子评论
            MomentComment replyComment = momentCommentMapper.selectById(replyId);
            ActorBase actorBaseTo = ContentActorUtils.getCommentActor(replyComment);
            if (Objects.nonNull(actorBaseTo.getActorId())) {
                Actor actorTo = userServiceClient.getActorById(actorBaseTo.getActorId(), actorBaseTo.getActorType()).getData();
                output.setActorTo(actorTo);
            }
        } else {
            // 是根评论，就要查询时刻的发布者
            Moment moment = momentService.getById(comment.getMomentId());
            Actor actorTo = userServiceClient.getActorById(moment.getUserId(), ActorType.USER.getCode()).getData();
            output.setActorTo(actorTo);
        }
        return output;
    }

    @Override
    public int getCommentCountByMomentId(Long momentId) {
        int commentCount = 0;
        List<MomentCommentOutput> commentList = momentCommentMapper.getCommentCountByMomentId(momentId);
        commentCount += commentList.size();
        long subRepliesCount = commentList.stream()
                .mapToLong(MomentCommentOutput::getReplyCount)
                .sum();
        commentCount += (int) subRepliesCount;
        return commentCount;
    }

    @Override
    public boolean deleteComment(Long momentId) {
        MomentComment comment = momentCommentMapper.selectById(momentId);
        Moment moment = momentService.getById(comment.getMomentId());
        Long currentUserId = SecurityUtils.getUserId();
        Long commentUserId = comment.getUserId();
        Long authorId = moment.getUserId();

        if (commentUserId.equals(currentUserId) || authorId.equals(currentUserId)) { // 仅自己和时刻的作者可删除
            int remove = momentCommentMapper.deleteById(momentId);
            if (remove > 0) {
                return true;
            } else {
                throw new SystemException(ResultCode.OPERATION_FAIL);
            }
        } else {
            throw new SystemException(ResultCode.FORBIDDEN);
        }
    }

    /**
     * 获取评论的子评论或跟评论的id
     * 如果回复的是子评论，那么优先查询被回复的子评论信息，如果回复的是根评论，那么就查询被回复的根评论的信息
     *
     * @param comment 评论
     * @return 被回复的评论的id
     */
    public Long getCommentReplyId(MomentComment comment) {
        return comment.getReplyId() > -1 ? comment.getReplyId() : comment.getRootId() > -1 ? comment.getRootId() : null;
    }


    @Override
    public Map<Long, Integer> batchGetCommentCount(List<Long> momentIds) {
        if (momentIds == null || momentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MomentCommentCountDTO> countList = momentCommentMapper.batchGetMomentCommentCount(momentIds);
        return countList.stream()
                .collect(Collectors.toMap(
                        MomentCommentCountDTO::getMomentId,
                        MomentCommentCountDTO::getCommentCount
                ));
    }

    /**
     * 填充 comment 的 actor 信息, 根据 actorId + actorType 从 actorMap 中查询 actor 信息
     *
     * @param comment         评论
     * @param actorMap        actorMap 包含用户和游客两个map
     * @param repliedActorMap 被回复的评论的map
     */
    public void fillCommentActor(MomentCommentOutput comment, Map<Integer, Map<Long, Actor>> actorMap, Map<Long, ActorBase> repliedActorMap) {
        ActorBase topActorBase = ContentActorUtils.getCommentActor(comment);
        comment.setActor(ActorUtils.getActorWithMap(topActorBase.getActorId(), topActorBase.getActorType(), actorMap));
        if (comment.getReplyId() != -1) {
            // 如果回复了某条评论，就把被回复人的信息查询出来
            ActorBase repliedActorBase = repliedActorMap.get(comment.getReplyId());
            comment.setActorTo(ActorUtils.getActorWithMap(repliedActorBase.getActorId(), repliedActorBase.getActorType(), actorMap));
        }
    }
}

