package com.youyu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.common.PageBase;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.message.MessageListOutput;
import com.youyu.entity.user.Message;
import com.youyu.entity.user.PositionInfo;
import com.youyu.enums.AdCode;
import com.youyu.mapper.MessageMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.MessageService;
import com.youyu.service.UserService;
import com.youyu.utils.PageUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

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

    @RequestMapping("/open/create")
    ResponseResult<Message> create(@Valid Message message) {
        boolean save = messageService.save(message);
        PositionInfo position = userService.getUserPositionByIP();
        message.setAdcode(position.getAdcode());
        return ResponseResult.success(message);
    }

    @RequestMapping("/open/update")
    ResponseResult<Message> update(@Valid Message message) {
        boolean update = messageService.updateById(message);
        return ResponseResult.success(message);
    }

    @RequestMapping("/open/list")
    ResponseResult<PageOutput<MessageListOutput>> list(PageBase input) {
        Page<Message> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<Message> messagePage = messageMapper.selectPage(page, null);
        PageOutput<MessageListOutput> pageOutput = PageUtils.setPageResult(messagePage, MessageListOutput.class);
        pageOutput.getList().forEach(item->item.setAdname(AdCode.getDescByCode(item.getAdcode())));
        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/delete")
    ResponseResult<Boolean> delete(@Valid Message message) {
        boolean delete = messageService.removeById(message);
        return ResponseResult.success(delete);
    }
}

