package com.youyu.service.moment.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.moment.MomentCommentListInput;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.dto.user.ActorBase;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.moment.MomentCommentLike;
import com.youyu.entity.moment.MomentUserOutput;
import com.youyu.entity.user.Actor;
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

import java.util.*;

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
    RabbitTemplate template;

    @Override
    public MomentCommentListOutput createComment(MomentComment input) {
        int save = momentCommentMapper.insert(input);
        if (save > 0) {
            MomentCommentListOutput detail = getCommentById(input.getId());
            // 如果不是回复自己，发送通知邮件
            if (!detail.getActor().getId().equals(detail.getActorTo().getId())) {
                template.convertAndSend("amq.direct", "momentCommentMail", detail);
            }
            return detail;
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public PageOutput<MomentCommentListOutput> momentCommentPage(MomentCommentListInput input) {
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<MomentComment> queryWrapper = new LambdaQueryWrapper<>();
        // 子评论只需要rootId就可以查询
        if (input.getRootId() != null) {
            queryWrapper.eq(MomentComment::getRootId, input.getRootId());
        } else {
            queryWrapper.eq(MomentComment::getMomentId, input.getMomentId());
            queryWrapper.eq(MomentComment::getRootId, -1);
        }
        queryWrapper.last("order by" + " " + input.getOrderBy() + " " + (input.isAsc() ? "asc" : "desc"));
        Page<MomentComment> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, queryWrapper);
        PageOutput<MomentCommentListOutput> pageOutput = PageUtils.setPageResult(page, MomentCommentListOutput.class);

        // 收集所有actor信息，一次性查询
        List<ActorBase> actorBases = new ArrayList<>(pageOutput.getList().stream().map(this::getCommentActor).toList());

        // 仅临时记录被回复的评论
        Map<Long, ActorBase> tempReplyActorMap = new HashMap<>();

        // 处理额外信息
        pageOutput.getList().forEach(item -> {
            item.setAdname(LocateUtils.getShortNameByCode(String.valueOf(item.getAdcode())));
            // 查询是否点赞
            if (!Objects.isNull(userId)) {
                MomentCommentLike momentCommentLike = new MomentCommentLike();
                momentCommentLike.setCommentId(item.getId());
                boolean like = momentCommentLikeService.isMomentCommentLike(momentCommentLike);
                item.setCommentLike(like);
            }
            // 根评论，查询回复数量，最早n条回复
            if (input.getRootId() == null) {
                LambdaQueryWrapper<MomentComment> replyQueryWrapper = new LambdaQueryWrapper<>();
                replyQueryWrapper.eq(MomentComment::getRootId, item.getId());
                replyQueryWrapper.orderByDesc(MomentComment::getCreateTime);
                Long replyCount = momentCommentMapper.selectCount(replyQueryWrapper);
                item.setReplyCount(replyCount);
                if (replyCount > 0) {
                    List<MomentComment> repliesList = momentCommentMapper.selectList(replyQueryWrapper.last("LIMIT 2"));
                    actorBases.addAll(repliesList.stream().map(this::getCommentActor).toList());
                    List<MomentCommentListOutput> children = BeanCopyUtils.copyBeanList(repliesList, MomentCommentListOutput.class);
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
                MomentComment repliedComment = momentCommentMapper.selectById(item.getReplyId());
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

    public MomentCommentListOutput getCommentById(Long commentId) {
        MomentComment comment = momentCommentMapper.selectById(commentId);
        MomentCommentListOutput output = BeanCopyUtils.copyBean(comment, MomentCommentListOutput.class);
        // 评论人信息查询
        ActorBase actorBase = getCommentActor(comment);
        MomentUserOutput actor = momentService.getMomentActor(actorBase.getActorId(), actorBase.getActorType(), false);
        output.setActor(actor);
        // 被评论人信息查询
        Long replyId = getCommentReplyId(comment);
        if (replyId != null) {
            // 不是根评论，而是子评论或回复了子评论
            MomentComment replyComment = momentCommentMapper.selectById(replyId);
            ActorBase actorBaseTo = getCommentActor(replyComment);
            if (Objects.nonNull(actorBaseTo.getActorId())) {
                MomentUserOutput actorTo = momentService.getMomentActor(actorBaseTo.getActorId(), actorBaseTo.getActorType(), false);
                output.setActorTo(actorTo);
            }
        } else {
            // 是根评论，就要查询时刻的发布者
            Moment moment = momentService.getById(comment.getMomentId());
            MomentUserOutput actorTo = momentService.getMomentActor(moment.getUserId(), ActorType.USER.getCode(), false);
            output.setActorTo(actorTo);
        }
        return output;
    }

    @Override
    public int getCommentCountByMomentId(Long momentId) {
        int commentCount = 0;
        List<MomentCommentListOutput> commentList = momentCommentMapper.getCommentCountByMomentId(momentId);
        commentCount += commentList.size();
        long subRepliesCount = commentList.stream()
                .mapToLong(MomentCommentListOutput::getReplyCount)
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

    /**
     * 获取评论的actor信息
     * 如果userId存在，说明是来自用户的评论，如果visitorId存在，说明是游客的评论
     *
     * @param comment 时刻评论
     * @return id
     */
    public ActorBase getCommentActor(MomentComment comment) {
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
    public void fillCommentActor(MomentCommentListOutput comment, Map<Integer, Map<Long, Actor>> actorMap, Map<Long, ActorBase> repliedActorMap) {
        ActorBase topActorBase = getCommentActor(comment);
        comment.setActor(ActorUtils.getActorWithMap(topActorBase.getActorId(), topActorBase.getActorType(), actorMap));
        if (comment.getReplyId() != -1) {
            // 如果回复了某条评论，就把被回复人的信息查询出来
            ActorBase repliedActorBase = repliedActorMap.get(comment.getReplyId());
            comment.setActorTo(ActorUtils.getActorWithMap(repliedActorBase.getActorId(), repliedActorBase.getActorType(), actorMap));
        }
    }
}

