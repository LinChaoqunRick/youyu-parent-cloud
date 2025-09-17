package com.youyu.service.message.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.message.MessageListInput;
import com.youyu.dto.message.MessageListOutput;
import com.youyu.entity.Visitor;
import com.youyu.entity.user.Message;
import com.youyu.entity.user.User;
import com.youyu.feign.UserServiceClient;
import com.youyu.mapper.message.MessageMapper;
import com.youyu.mapper.message.VisitorMapper;
import com.youyu.service.message.MessageService;
import com.youyu.utils.DateUtils;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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

    @Resource
    private VisitorMapper visitorMapper;

    @Override
    public PageOutput<MessageListOutput> pageMessage(MessageListInput input) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getRootId, input.getRootId());
        queryWrapper.orderByDesc(Message::getCreateTime);
        // 状态
        if (input.getStatus() != null) {
            queryWrapper.eq(Message::getStatus, input.getStatus());
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
        List<Message> messageList = messagePage.getRecords();
        // 查询用户/游客信息并转化成map方便获取
        List<Long> userIds = messageList.stream().map(Message::getUserId).toList();
        List<Long> visitorIds = messageList.stream().map(Message::getVisitorId).toList();
        List<User> users = userServiceClient.listByIds(userIds).getData();
        List<Visitor> visitors = visitorMapper.selectBatchIds(visitorIds);
        Map<Long, User> userMap = CollStreamUtil.toMap(users, User::getId, user -> user);
        Map<Long, Visitor> visitorMap = CollStreamUtil.toMap(visitors, Visitor::getId, visitor -> visitor);
        // 封装结果
        PageOutput<MessageListOutput> pageOutput = PageUtils.setPageResult(messagePage, MessageListOutput.class);
        IntStream.range(0, pageOutput.getList().size()).forEach(i -> {
            Message message = messageList.get(i);
            MessageListOutput item = pageOutput.getList().get(i);
            item.setAdname(LocateUtils.getShortNameByCode(String.valueOf(item.getAdcode())));
            if (message.getUserId() != null) {
                User user = userMap.get(message.getUserId());
                item.setUserId(user.getId());
                item.setNickname(user.getNickname());
                item.setAvatar(user.getAvatar());
            } else if (message.getVisitorId() != null) {
                Visitor visitor = visitorMap.get(message.getVisitorId());
                item.setNickname(visitor.getNickname());
                item.setAvatar(visitor.getAvatar());
            }
        });

        return pageOutput;
    }
}

