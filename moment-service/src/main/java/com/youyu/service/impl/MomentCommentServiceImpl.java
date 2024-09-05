package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.moment.MomentCommentListInput;
import com.youyu.dto.moment.MomentCommentListOutput;
import com.youyu.dto.moment.MomentReplyListInput;
import com.youyu.dto.common.PageOutput;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.moment.MomentComment;
import com.youyu.entity.moment.MomentCommentLike;
import com.youyu.entity.moment.MomentUserOutput;
import com.youyu.enums.AdCode;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.MailServiceClient;
import com.youyu.mapper.MomentCommentMapper;
import com.youyu.service.MomentCommentLikeService;
import com.youyu.service.MomentCommentService;
import com.youyu.service.MomentService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.PageUtils;
import com.youyu.utils.SecurityUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
        queryWrapper.eq(MomentComment::getMomentId, input.getMomentId());
        queryWrapper.eq(MomentComment::getRootId, -1);
        queryWrapper.last("order by" + " " + input.getOrderBy() + " " + (input.isAsc() ? "asc" : "desc"));
        // 分页查询
        Page<MomentComment> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, queryWrapper);

        // 封装查询结果
        PageOutput<MomentCommentListOutput> pageOutput = PageUtils.setPageResult(page, MomentCommentListOutput.class);

        List<Long> userIds = pageOutput.getList().stream().map(MomentComment::getUserId).collect(Collectors.toList());
        Map<Long, MomentUserOutput> userMap = createUserMap(userIds);

        setExtraData(pageOutput.getList(), userMap);

        return pageOutput;
    }

    @Override
    public List<MomentCommentListOutput> listMomentCommentAll(MomentCommentListInput input) {
        LambdaQueryWrapper<MomentComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MomentComment::getMomentId, input.getMomentId());
        queryWrapper.eq(MomentComment::getRootId, -1);
        queryWrapper.last("order by" + " " + input.getOrderBy() + " " + (input.isAsc() ? "asc" : "desc"));
        List<MomentComment> list = momentCommentMapper.selectList(queryWrapper);
        List<Long> userIds = list.stream().map(MomentComment::getUserId).collect(Collectors.toList());
        Map<Long, MomentUserOutput> userMap = createUserMap(userIds);

        List<MomentCommentListOutput> outputList = BeanCopyUtils.copyBeanList(list, MomentCommentListOutput.class);

        setExtraData(outputList, userMap);
        return outputList;
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
        Long subRepliesCount = commentList.stream()
                .mapToLong(MomentCommentListOutput::getReplyCount)
                .sum();
        commentCount += subRepliesCount;
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

    @Override
    public PageOutput<MomentCommentListOutput> listMomentReplyPage(MomentReplyListInput input) {
        LambdaQueryWrapper<MomentComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MomentComment::getRootId, input.getId());
        queryWrapper.last("order by" + " " + input.getOrderBy() + " " + (input.isAsc() ? "asc" : "desc"));
        // 分页查询
        Page<MomentComment> page = new Page<>(input.getPageNum(), input.getPageSize());
        page(page, queryWrapper);

        // 封装查询结果
        PageOutput<MomentCommentListOutput> pageOutput = PageUtils.setPageResult(page, MomentCommentListOutput.class);

        List<Long> userIds = new ArrayList<>();
        userIds.addAll(pageOutput.getList().stream().map(MomentComment::getUserId).collect(Collectors.toList()));
        userIds.addAll(pageOutput.getList().stream().map(MomentComment::getUserIdTo).collect(Collectors.toList()));
        Map<Long, MomentUserOutput> userMap = createUserMap(userIds);

        setExtraData(pageOutput.getList(), userMap);

        return pageOutput;
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

    public void setExtraData(List<MomentCommentListOutput> list, Map<Long, MomentUserOutput> userMap) {
        Long userId = SecurityUtils.getUserId();

        list.forEach(item -> {
            item.setUser(userMap.get(item.getUserId()));
            item.setUserTo(userMap.get(item.getUserIdTo()));
            item.setAdname(AdCode.getDescByCode(item.getAdcode()));

            // 是否点赞了
            if (!Objects.isNull(userId)) {
                MomentCommentLike momentCommentLike = new MomentCommentLike();
                momentCommentLike.setCommentId(item.getId());
                boolean like = momentCommentLikeService.isMomentCommentLike(momentCommentLike);
                item.setCommentLike(like);
            }

            // 设置回复数量
            LambdaQueryWrapper<MomentComment> replyQueryWrapper = new LambdaQueryWrapper<>();
            replyQueryWrapper.eq(MomentComment::getRootId, item.getId());
            item.setReplyCount(momentCommentMapper.selectCount(replyQueryWrapper));
        });
    }
}

