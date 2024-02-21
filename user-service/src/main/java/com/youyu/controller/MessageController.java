package com.youyu.controller;

import com.youyu.entity.user.Message;
import com.youyu.result.ResponseResult;
import com.youyu.service.MessageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

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

    @RequestMapping("/create")
    ResponseResult<Message> create(@Valid Message message) {
        boolean save = messageService.save(message);
        return ResponseResult.success(message);
    }

    @RequestMapping("/update")
    ResponseResult<Message> update(@Valid Message message) {
        boolean update = messageService.updateById(message);
        return ResponseResult.success(message);
    }

    @RequestMapping("/delete")
    ResponseResult<Boolean> delete(@Valid Message message) {
        boolean delete = messageService.removeById(message);
        return ResponseResult.success(delete);
    }
}

