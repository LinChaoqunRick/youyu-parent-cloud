package com.youyu.controller.message;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youyu.annotation.Log;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.message.MessageListInput;
import com.youyu.dto.message.MessageListOutput;
import com.youyu.entity.user.Message;
import com.youyu.enums.LogType;
import com.youyu.result.ResponseResult;
import com.youyu.service.message.MessageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    @RequestMapping("/page")
    ResponseResult<PageOutput<MessageListOutput>> list(@Valid MessageListInput input) {
        return ResponseResult.success(messageService.pageMessage(input));
    }

    @RequestMapping("/showHide")
    @Log(title = "显隐留言", type = LogType.UPDATE)
    public ResponseResult<Boolean> showHide(@RequestParam("ids") String ids, @RequestParam("status") Integer status) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .filter(StringUtils::hasText) // 过滤空值
                .map(Long::valueOf)              // 转换成 Long
                .collect(Collectors.toList());

        boolean update = messageService.update(
                new LambdaUpdateWrapper<Message>()
                        .in(Message::getId, idList)
                        .set(Message::getStatus, status)
        );

        return ResponseResult.success(update);
    }


    @RequestMapping("/delete")
    @Log(title = "删除留言", type = LogType.DELETE)
    ResponseResult<Boolean> delete(@RequestParam("ids") String ids) {
        boolean delete = messageService.removeBatchByIds(Arrays.stream(ids.split(",")).toList());
        return ResponseResult.success(delete);
    }
}

