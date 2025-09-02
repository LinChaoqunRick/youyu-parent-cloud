package com.youyu.controller.logManage;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.common.PageOutput;
import com.youyu.dto.logManage.LogPageInput;
import com.youyu.entity.Logs;
import com.youyu.result.ResponseResult;
import com.youyu.service.LogsService;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.PageUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

@RestController
@RequestMapping("/manage/logs")
public class LogController {

    @Resource
    private LogsService logsService;

    @RequestMapping("/page")
    public ResponseResult<PageOutput<Logs>> areaAccess(@Valid LogPageInput input) {
        // 处理结束时间（左闭右开：endTime +1 天）
        LocalDate endDate = input.getEndTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(1);
        Date endTimePlusOne = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LambdaQueryWrapper<Logs> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(input.getType())) {
            String[] types = input.getType().split(",");
            wrapper.in(Logs::getType, Arrays.asList(types));
        }

        if (input.getStatus() != null) {
            wrapper.eq(Logs::getResult, input.getStatus());
        }

        if (StringUtils.hasText(input.getAreaCodes())) {
            String[] codes = input.getAreaCodes().split(",");
            if (codes.length > 0) {
                wrapper.in(Logs::getAdcode, Arrays.asList(codes));
            }
        }

        if (input.getStartTime() != null && input.getEndTime() != null) {
            wrapper.between(Logs::getCreateTime, input.getStartTime(), endTimePlusOne);
        }

        // keyword 模糊查询
        if (StringUtils.hasText(input.getKeyword())) {
            String keyword = input.getKeyword();
            wrapper.and(q -> q.like(Logs::getName, keyword)
                    .or().like(Logs::getIp, keyword)
                    .or().like(Logs::getPath, keyword)
                    .or().like(Logs::getUserId, keyword));
        }

        // 按时间倒序
        wrapper.orderByDesc(Logs::getCreateTime);

        Page<Logs> page = new Page<>(input.getPageNum(), input.getPageSize());
        Page<Logs> postPage = logsService.page(page, wrapper);

        PageOutput<Logs> pageOutput = PageUtils.setPageResult(postPage, Logs.class);

        return ResponseResult.success(pageOutput);
    }
}
