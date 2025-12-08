package com.youyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyu.dto.analysis.RegionData;
import com.youyu.dto.analysis.VisitData;
import com.youyu.entity.user.Visitor;
import com.youyu.mapper.VisitorMapper;
import com.youyu.service.VisitorService;
import com.youyu.utils.LocateUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * (Visitor)表服务实现类
 *
 * @author makejava
 * @since 2025-09-16 17:27:20
 */
@Service("visitorService")
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    @Resource
    private VisitorMapper visitorMapper;

    @Override
    public Visitor getVisitorByEmail(String email) {
        LambdaQueryWrapper<Visitor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Visitor::getEmail, email);
        return visitorMapper.selectOne(queryWrapper);
    }

    @Override
    public Visitor saveOrUpdateByEmail(Visitor visitor) {
        LambdaQueryWrapper<Visitor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Visitor::getEmail, visitor.getEmail());
        Visitor selectOne = visitorMapper.selectOne(queryWrapper);
        if (selectOne != null) {
            visitor.setUpdateTime(new Date());
            visitor.setId(selectOne.getId());
            visitorMapper.updateById(visitor);
        } else {
            visitorMapper.insert(visitor);
        }
        return visitor;
    }

    @Override
    public Long getVisitorTotal() {
        return visitorMapper.selectCount(new LambdaQueryWrapper<Visitor>()
                .eq(Visitor::getDeleted, 0));
    }

    @Override
    public List<VisitData> getMonthlyNewVisitors() {
        List<VisitData> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 0; i < 12; i++) {
            YearMonth yearMonth = YearMonth.now().minusMonths(11 - i);
            LocalDate firstDay = yearMonth.atDay(1);
            LocalDate lastDay = yearMonth.atEndOfMonth();

            LocalDateTime startOfMonth = firstDay.atStartOfDay();
            LocalDateTime endOfMonth = lastDay.plusDays(1).atStartOfDay();

            // 将LocalDateTime转换为Date
            Date startDate = Date.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endOfMonth.atZone(ZoneId.systemDefault()).toInstant());

            // 统计该月新增游客数量
            long count = visitorMapper.selectCount(new LambdaQueryWrapper<Visitor>()
                    .ge(Visitor::getCreateTime, startDate)
                    .lt(Visitor::getCreateTime, endDate)
                    .eq(Visitor::getDeleted, 0));

            VisitData data = new VisitData();
            data.setTime(yearMonth.format(formatter));
            data.setVisitCount(count);
            result.add(data);
        }

        return result;
    }

    @Override
    public List<RegionData> getVisitorsByProvince() {
        // 查询所有游客的adcode（只查询adcode不为null的记录）
        List<Visitor> visitors = visitorMapper.selectList(new LambdaQueryWrapper<Visitor>()
                .isNotNull(Visitor::getAdcode)
                .select(Visitor::getAdcode));

        // 按省份分组统计（添加null检查）
        Map<Integer, Long> provinceMap = visitors.stream()
                .filter(visitor -> visitor != null && visitor.getAdcode() != null)
                .map(visitor -> getProvinceAdcode(visitor.getAdcode()))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        provinceAdcode -> provinceAdcode,
                        Collectors.counting()));

        // 转换为结果列表并按数量降序排序
        return provinceMap.entrySet().stream()
                .map(entry -> {
                    RegionData data = new RegionData();
                    data.setAdcode(entry.getKey());
                    data.setAdName(LocateUtils.getShortNameByCode(String.valueOf(entry.getKey())));
                    data.setVisitCount(entry.getValue());
                    return data;
                })
                .sorted((a, b) -> b.getVisitCount().compareTo(a.getVisitCount()))
                .collect(Collectors.toList());
    }

    /**
     * 将行政编码转换为省份级别（最后4位为00）
     */
    private Integer getProvinceAdcode(Integer adcode) {
        if (adcode == null) {
            return null;
        }
        String adcodeStr = String.format("%06d", adcode);
        if (adcodeStr.length() >= 4) {
            return Integer.parseInt(adcodeStr.substring(0, 4) + "00");
        }
        return adcode;
    }
}

