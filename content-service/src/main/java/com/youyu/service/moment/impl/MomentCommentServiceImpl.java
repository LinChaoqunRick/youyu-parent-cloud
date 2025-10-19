package com.youyu.service.moment.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.moment.MomentCommentListInput;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.moment.MomentCommentLike;
import com.youyu.entity.moment.MomentUserOutput;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.mapper.moment.MomentCommentMapper;
import com.youyu.service.moment.MomentCommentLikeService;
import com.youyu.service.moment.MomentCommentService;
import com.youyu.service.moment.MomentService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    RabbitTemplate template;

    @Override
    public MomentCommentListOutput createComment(MomentComment input) {
        int save = momentCommentMapper.insert(input);
        if (save > 0) {
            MomentCommentListOutput detail = getCommentDetailByCommentId(input.getId());
            if (!input.getUserId().equals(input.getUserIdTo())) {
                template.convertAndSend("amq.direct", "momentCommentMail", detail);
            }
            return detail;
        } else {
            throw new SystemException(ResultCode.OPERATION_FAIL);
        }
    }

    @Override
    public PageOutput<MomentCommentListOutput> listMomentCommentPage(MomentCommentListInput input) {
        LambdaQueryWrapper<MomentComment> queryWrapper = new LambdaQueryWrapper<>();
        // 子评论只需要rootId就可以查询
        if (input.getRootId() != null) {
            queryWrapper.eq(MomentComment::getRootId, input.getRootId());
        } else {
            queryWrapper.eq(MomentComment::getMomentId, input.getMomentId());
            queryWrapper.eq(MomentComment::getRootId, -1);
        }
        queryWrapper.last("order by" + " " + input.getOrderBy() + " " + (input.isAsc() ? "asc" : "desc"));
        // 分页查询
        Page<MomentComment> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, queryWrapper);
        // 封装查询结果
        PageOutput<MomentCommentListOutput> pageOutput = PageUtils.setPageResult(page, MomentCommentListOutput.class);
        // 收集所有用户id，一次性查询
        List<Long> userIds = pageOutput.getList().stream()
                .flatMap(c -> Stream.of(c.getUserId(), c.getUserIdTo()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Long userId = SecurityUtils.getUserId();
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
            // 查询回复数量
            LambdaQueryWrapper<MomentComment> replyQueryWrapper = new LambdaQueryWrapper<>();
            replyQueryWrapper.eq(MomentComment::getRootId, item.getId());
            replyQueryWrapper.orderByDesc(MomentComment::getCreateTime);
            Long replyCount = momentCommentMapper.selectCount(replyQueryWrapper);
            item.setReplyCount(replyCount);
            // 查询最早的n条回复
            if (replyCount > 0) {
                List<MomentComment> repliesList = momentCommentMapper.selectList(replyQueryWrapper.last("LIMIT 2"));
                List<MomentCommentListOutput> children = BeanCopyUtils.copyBeanList(repliesList, MomentCommentListOutput.class);
                children.forEach(child -> {
                    child.setAdname(LocateUtils.getShortNameByCode(String.valueOf(child.getAdcode())));
                    userIds.add(child.getUserId());
                    userIds.add(child.getUserIdTo());
                });
                item.setChildren(children);
            }
        });
        // 查询所有涉及到的用户信息
        Map<Long, MomentUserOutput> userMap = createUserMap(userIds);
        // 填充用户信息
        pageOutput.getList().forEach(item -> {
            item.setUser(userMap.get(item.getUserId()));
            item.setUserTo(userMap.get(item.getUserIdTo()));
            if (item.getChildren() != null) {
                item.getChildren().forEach(child -> {
                    child.setUser(userMap.get(child.getUserId()));
                    child.setUserTo(userMap.get(child.getUserIdTo()));
                });
            }
        });

        return pageOutput;
    }

    @Override
    public MomentCommentListOutput getCommentDetailByCommentId(Long commentId) {
        MomentComment comment = momentCommentMapper.selectById(commentId);
        MomentCommentListOutput output = BeanCopyUtils.copyBean(comment, MomentCommentListOutput.class);

        MomentUserOutput user = momentService.getMomentUserDetailById(comment.getUserId(), false);
        output.setUser(user);

        if (Objects.nonNull(comment.getUserIdTo()) && comment.getUserIdTo() > -1) {
            MomentUserOutput userTo = momentService.getMomentUserDetailById(comment.getUserIdTo(), false);
            output.setUserTo(userTo);
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
     * 根据userIds获取map信息
     *
     * @param userIds
     * @return
     */
    public Map<Long, MomentUserOutput> createUserMap(List<Long> userIds) {
        Map<Long, MomentUserOutput> userMap = new HashMap<>();
        userIds.stream()
                .distinct()
                .filter(id -> id != -1)
                .forEach(id -> {
                    userMap.put(id, momentService.getMomentUserDetailById(id, false));
                });
        return userMap;
    }
}

