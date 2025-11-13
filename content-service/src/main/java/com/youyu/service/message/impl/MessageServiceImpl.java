package com.youyu.service.message.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.message.MessageListInput;
import com.youyu.dto.message.MessageListOutput;
import com.youyu.entity.user.Visitor;
import com.youyu.entity.user.Message;
import com.youyu.entity.user.User;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.message.MessageMapper;
import com.youyu.service.message.MessageService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.DateUtils;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.PageUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * (Message)表服务实现类
 *
 * @author makejava
 * @since 2024-02-20 22:04:04
 */
@Service("messageService")
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private MessageMapper messageMapper;

    @Value("${message.replyCount}")
    private Long replyCount;

    @Override
    public PageOutput<MessageListOutput> pageMessage(MessageListInput input) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Message::getCreateTime);
        // 状态
        if (input.getStatus() != null) {
            queryWrapper.eq(Message::getStatus, input.getStatus());
        }
        // rootId
        if (input.getRootId() != null) {
            queryWrapper.eq(Message::getRootId, input.getRootId());
        }
        // 时间范围
        if (input.getStartTime() != null && input.getEndTime() != null) {
            Date endTimePlusOne = DateUtils.datePlusOne(input.getEndTime());
            queryWrapper.between(Message::getCreateTime, input.getStartTime(), endTimePlusOne);
        }
        // keyword 模糊查询
        if (StringUtils.hasText(input.getKeyword())) {
            String keyword = input.getKeyword();
            queryWrapper.and(q -> q.like(Message::getContent, keyword)
                    .or().like(Message::getUserId, keyword));
        }
        // 查询留言信息
        Page<Message> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<Message> messagePage = messageMapper.selectPage(page, queryWrapper);
        // 封装结果
        PageOutput<MessageListOutput> pageOutput = PageUtils.setPageResult(messagePage, MessageListOutput.class);
        fillMessageInfo(pageOutput.getList());
        return pageOutput;
    }

    @Override
    public Long countReplies(Long rootId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getRootId, rootId);
        return messageMapper.selectCount(queryWrapper);
    }

    @Override
    public List<MessageListOutput> selectLatestReplies(Long id) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getRootId, id)
                .orderByDesc(Message::getCreateTime) // 按时间倒序
                .last("LIMIT " + replyCount); // 限制replyCount条;
        List<Message> latestReplies = messageMapper.selectList(queryWrapper);
        List<MessageListOutput> replyListOutputs = BeanCopyUtils.copyBeanList(latestReplies, MessageListOutput.class);
        fillMessageInfo(replyListOutputs);
        return replyListOutputs;
    }

    private void fillMessageInfo(List<MessageListOutput> messageList) {
        // 查询用户/游客信息并转化成map方便获取
        List<Long> userIds = messageList.stream().map(MessageListOutput::getUserId).toList();
        List<Long> visitorIds = messageList.stream().map(MessageListOutput::getVisitorId).toList();
        List<User> users = userIds.isEmpty() ? new ArrayList<>() : userServiceClient.listByIds(userIds).getData();
        List<Visitor> visitors = visitorIds.isEmpty() ? new ArrayList<>() : userServiceClient.selectBatchIds(visitorIds).getData();
        Map<Long, User> userMap = CollStreamUtil.toMap(users, User::getId, user -> user);
        Map<Long, Visitor> visitorMap = CollStreamUtil.toMap(visitors, Visitor::getId, visitor -> visitor);
        // 填充信息
        messageList.forEach(item -> {
            item.setAdname(LocateUtils.getShortNameByCode(String.valueOf(item.getAdcode())));
            if (item.getUserId() != null) {
                User user = userMap.get(item.getUserId());
                item.setUserId(user.getId());
                item.setNickname(user.getNickname());
                item.setAvatar(user.getAvatar());
            } else if (item.getVisitorId() != null) {
                Visitor visitor = visitorMap.get(item.getVisitorId());
                item.setNickname(visitor.getNickname());
                item.setAvatar(visitor.getAvatar());
            }
            // 根评论还需要填充子评论信息
            if (item.getRootId() == -1) {
                Long replyCount = countReplies(item.getId());
                List<MessageListOutput> replyList = selectLatestReplies(item.getId());
                item.setReplyCount(replyCount);
                item.setChildren(replyList);
            }
        });
    }
}

