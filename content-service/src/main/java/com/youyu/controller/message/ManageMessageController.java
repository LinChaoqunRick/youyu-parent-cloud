package com.youyu.controller.message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.annotation.Log;
import com.youyu.dto.common.PageBase;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.message.MessageListInput;
import com.youyu.dto.message.MessageListOutput;
import com.youyu.dto.message.MessageUserOutput;
import com.youyu.entity.user.Message;
import com.youyu.enums.LogType;
import com.youyu.mapper.message.MessageMapper;
import com.youyu.result.ResponseResult;
import com.youyu.service.message.MessageService;
import com.youyu.utils.DateUtils;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.PageUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

    @Resource
    private MessageMapper messageMapper;

    @RequestMapping("/page")
    ResponseResult<PageOutput<MessageListOutput>> list(@Valid MessageListInput input) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.orderByDesc(Message::getCreateTime);

        if (input.getStatus() != null) {
            queryWrapper.eq(Message::getStatus, input.getStatus());
        }

        if (input.getStartTime() != null && input.getEndTime() != null) {
            Date endTimePlusOne = DateUtils.datePlusOne(input.getEndTime());
            queryWrapper.between(Message::getCreateTime, input.getStartTime(), endTimePlusOne);
        }

        // keyword 模糊查询
        if (StringUtils.hasText(input.getKeyword())) {
            String keyword = input.getKeyword();
            queryWrapper.and(q -> q.like(Message::getContent, keyword)
                    .or().like(Message::getNickname, keyword)
                    .or().like(Message::getUserId, keyword));
        }

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

