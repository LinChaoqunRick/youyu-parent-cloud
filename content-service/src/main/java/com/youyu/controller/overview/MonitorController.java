package com.youyu.controller.overview;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youyu.dto.overview.AreaAccessInput;
import com.youyu.dto.overview.AreaAccessOutput;
import com.youyu.entity.Logs;
import com.youyu.enums.LogType;
import com.youyu.result.ResponseResult;
import com.youyu.service.LogsService;
import com.youyu.utils.LocateUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manage/monitor")
public class MonitorController {

    @Resource
    private LogsService logsService;

    @RequestMapping("/areaAccess")
    public ResponseResult<List<AreaAccessOutput>> areaAccess(@Valid AreaAccessInput input) {
        // 处理结束时间（左闭右开：endTime +1 天）
        LocalDate endDate = input.getEndTime().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .plusDays(1);
        Date endTimePlusOne = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        QueryWrapper<Logs> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("adcode", "COUNT(*) AS cnt")
                .eq("type", LogType.ACCESS.getCode())
                .ne("adcode", "-1")
                .ge("create_time", input.getStartTime())
                .lt("create_time", endTimePlusOne)
                .groupBy("adcode");

        List<Map<String, Object>> result = logsService.listMaps(queryWrapper);

        List<AreaAccessOutput> accessOutputs = result.stream()
                .map((item) -> {
                    String adcode = item.get("adcode").toString();
                    String adName = LocateUtils.getNameByCode(adcode);
                    Long count = (Long) item.get("cnt");
                    return new AreaAccessOutput(adcode, adName, count);
                })
                .sorted(Comparator.comparing(AreaAccessOutput::getCount).reversed())
                .toList();

        return ResponseResult.success(accessOutputs);
    }
}
