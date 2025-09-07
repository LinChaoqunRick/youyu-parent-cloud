package com.youyu.controller.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.annotation.Log;
import com.youyu.dto.common.PageBase;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.message.MessageListOutput;
import com.youyu.dto.message.MessageUserOutput;
import com.youyu.entity.user.Message;
import com.youyu.enums.LogType;
import com.youyu.mapper.message.MessageMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.message.MessageService;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.PageUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * (Message)表控制层
 *
 * @author makejava
 * @since 2024-02-20 22:03:57
 */
@RestController
@RequestMapping("/manage/message")
public class ManageMessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private MessageMapper messageMapper;

    @RequestMapping("/page")
    ResponseResult<PageOutput<MessageListOutput>> list(PageBase input) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Message::getCreateTime);

        Page<Message> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<Message> messagePage = messageMapper.selectPage(page, queryWrapper);
        PageOutput<MessageListOutput> pageOutput = PageUtils.setPageResult(messagePage, MessageListOutput.class);

        pageOutput.getList().forEach(item -> {
            item.setAdname(LocateUtils.getShortNameByCode(String.valueOf(item.getAdcode())));
            if (Objects.nonNull(item.getUserId())) {
                MessageUserOutput userDetail = messageService.getUserDetail(item.getUserId());
                item.setUserInfo(userDetail);
            }
        });

        return ResponseResult.success(pageOutput);
    }

    @RequestMapping("/showHide")
    @Log(title = "显隐留言", type = LogType.UPDATE)
    ResponseResult<Boolean> showHide(Long id, Integer status) {
        boolean update = messageService.update(
                new LambdaUpdateWrapper<Message>()
                        .eq(Message::getId, id)
                        .set(Message::getStatus, status)
        );
        return ResponseResult.success(update);
    }

    @RequestMapping("/delete")
    @Log(title = "删除留言", type = LogType.DELETE)
    ResponseResult<Boolean> delete(@Valid Message message) {
        boolean delete = messageService.removeById(message);
        return ResponseResult.success(delete);
    }
}

