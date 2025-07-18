package com.youyu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.common.PageBase;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.message.MessageListOutput;
import com.youyu.dto.message.MessageUserOutput;
import com.youyu.entity.user.Message;
import com.youyu.entity.user.PositionInfo;
import com.youyu.enums.AdCode;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.mapper.MessageMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.MessageService;
import com.youyu.service.UserService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.PageUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Objects;

/**
 * (Message)表控制层
 *
 * @author makejava
 * @since 2024-02-20 22:03:57
 */
@RestController
@RequestMapping("message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private UserService userService;

    @Resource
    private LocateUtils locateUtils;

    @RequestMapping("/open/create")
    ResponseResult<MessageListOutput> create(@Valid Message message) {
        if (Objects.isNull(message.getUserId())) { // 如果是游客
            if (Objects.isNull(message.getNickname())) {
                throw new SystemException(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), "昵称不能为空");
            } else if (Objects.isNull(message.getAvatar())) {
                throw new SystemException(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), "头像不能为空");
            } else if (Objects.isNull(message.getEmail())) {
                throw new SystemException(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), "邮箱不能为空");
            }
        }
        PositionInfo position = locateUtils.getUserPositionByIP();
        message.setAdcode(position.getAdcode());
        messageService.save(message);
        MessageListOutput output = BeanCopyUtils.copyBean(message, MessageListOutput.class);
        if (Objects.nonNull(output.getUserId())) {
            MessageUserOutput userDetail = messageService.getUserDetail(message.getUserId());
            output.setUserInfo(userDetail);
        }
        return ResponseResult.success(output);
    }

    @RequestMapping("/open/update")
    ResponseResult<Message> update(@Valid Message message) {
        boolean update = messageService.updateById(message);
        return ResponseResult.success(message);
    }

    @RequestMapping("/open/list")
    ResponseResult<PageOutput<MessageListOutput>> list(PageBase input) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getRootId, -1);
        queryWrapper.orderByDesc(Message::getCreateTime);

        Page<Message> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<Message> messagePage = messageMapper.selectPage(page, queryWrapper);
        PageOutput<MessageListOutput> pageOutput = PageUtils.setPageResult(messagePage, MessageListOutput.class);

        pageOutput.getList().forEach(item -> {
            item.setAdname(AdCode.getDescByCode(item.getAdcode()));
            if (Objects.nonNull(item.getUserId())) {
                MessageUserOutput userDetail = messageService.getUserDetail(item.getUserId());
                item.setUserInfo(userDetail);
            }
        });

        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/open/getById")
    ResponseResult<MessageListOutput> getById(Long id) {
        Message message = messageService.getById(id);
        MessageListOutput output = BeanCopyUtils.copyBean(message, MessageListOutput.class);

        output.setAdname(AdCode.getDescByCode(output.getAdcode()));
        if (Objects.nonNull(output.getUserId())) {
            MessageUserOutput userDetail = messageService.getUserDetail(output.getUserId());
            output.setUserInfo(userDetail);
        }

        return ResponseResult.success(output);
    }

    @RequestMapping("/delete")
    ResponseResult<Boolean> delete(@Valid Message message) {
        boolean delete = messageService.removeById(message);
        return ResponseResult.success(delete);
    }
}

