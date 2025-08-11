package com.youyu.controller.overview;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youyu.dto.overview.AreaAccessInput;
import com.youyu.entity.Logs;
import com.youyu.enums.LogType;
import com.youyu.result.ResponseResult;
import com.youyu.service.LogsService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/manage/monitor")
public class MonitorController {

    @Resource
    private LogsService logsService;

    @RequestMapping("/areaAccess")
    public ResponseResult<Map<String, Long>> areaAccess(@Valid AreaAccessInput input) {
        QueryWrapper<Logs> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("adcode", "COUNT(*) AS cnt")
                .eq("type", LogType.ACCESS.getCode())
                .ge("create_time", input.getStartTime())
                .lt("create_time", input.getEndTime())
                .groupBy("adcode");

        List<Map<String, Object>> result = logsService.listMaps(queryWrapper);

        Map<String, Long> areaCountMap = result.stream()
                .collect(Collectors.toMap(
                        m -> String.valueOf(m.get("adcode")),
                        m -> ((Number) m.get("cnt")).longValue()
                ));
        return ResponseResult.success(areaCountMap);
    }
}
