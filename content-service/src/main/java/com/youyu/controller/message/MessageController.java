package com.youyu.controller.message;

import com.youyu.annotation.Log;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.message.CreateMessageInput;
import com.youyu.dto.message.MessageListInput;
import com.youyu.dto.message.MessageListOutput;
import com.youyu.entity.Visitor;
import com.youyu.entity.result.TencentLocationResult;
import com.youyu.entity.user.Message;
import com.youyu.enums.LogType;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.result.ResponseResult;
import com.youyu.service.message.MessageService;
import com.youyu.service.message.VisitorService;
import com.youyu.utils.BeanCopyUtils;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.lang.reflect.InvocationTargetException;
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
    private LocateUtils locateUtils;

    @Resource
    private VisitorService visitorService;

    @RequestMapping("/open/create")
    @Log(title = "新增留言", type = LogType.INSERT)
    ResponseResult<Boolean> create(@Valid CreateMessageInput input) throws InvocationTargetException, IllegalAccessException {
        Message message = BeanCopyUtils.copyBean(input, Message.class);
        if (SecurityUtils.isContentAdmin()) {
            message.setStatus(1);
        }
        boolean isVisitor = SecurityUtils.getUserId() == null; // 未登录，游客
        TencentLocationResult position = locateUtils.queryTencentIp();
        // 校验
        if (isVisitor) {
            if (Objects.isNull(input.getNickname())) {
                throw new SystemException(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), "昵称不能为空");
            } else if (Objects.isNull(input.getAvatar())) {
                throw new SystemException(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), "头像不能为空");
            } else if (Objects.isNull(input.getEmail())) {
                throw new SystemException(ResultCode.INVALID_METHOD_ARGUMENT.getCode(), "邮箱不能为空");
            }

            Visitor visitor = visitorService.getVisitorByEmail(input.getEmail());
            if (visitor == null) {
                // 新增：新游客留言
                visitor = BeanCopyUtils.copyBean(input, Visitor.class);
            } else {
                // 更新：旧游客留言
                BeanUtils.copyProperties(visitor, input);
            }
            visitor.setAdcode(position.getAdcode());
            visitorService.saveOrUpdate(visitor);
            message.setVisitorId(visitor.getId());
        } else {
            message.setUserId(SecurityUtils.getUserId());
        }

        message.setAdcode(position.getAdcode());
        boolean save = messageService.save(message);

        return ResponseResult.success(save);
    }

    @RequestMapping("/open/update")
    @Log(title = "更新留言", type = LogType.UPDATE)
    ResponseResult<Boolean> update(@Valid Message message) {
        boolean update = messageService.updateById(message);
        return ResponseResult.success(update);
    }

    @RequestMapping("/open/list")
    ResponseResult<PageOutput<MessageListOutput>> list(MessageListInput input) {
        input.setStatus(1);
        return ResponseResult.success(messageService.pageMessage(input));
    }

    @RequestMapping("/delete")
    @Log(title = "删除留言", type = LogType.DELETE)
    ResponseResult<Boolean> delete(@Valid Message message) {
        boolean delete = messageService.removeById(message);
        return ResponseResult.success(delete);
    }

    @RequestMapping("/open/getVisitorByEmail")
    ResponseResult<Visitor> getVisitorByEmail(@RequestParam String email) {
        Visitor visitor = visitorService.getVisitorByEmail(email);
        return ResponseResult.success(visitor);
    }
}

