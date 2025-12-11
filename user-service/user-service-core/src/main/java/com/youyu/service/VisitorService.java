package com.youyu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youyu.dto.RegionData;
import com.youyu.dto.VisitData;
import com.youyu.entity.user.Visitor;

import java.util.List;

/**
 * (Visitor)表服务接口
 *
 * @author makejava
 * @since 2025-09-16 17:27:20
 */
public interface VisitorService extends IService<Visitor> {
    Visitor getVisitorByEmail(String email);
    Visitor saveOrUpdateByEmail(Visitor visitor);

    /**
     * 获取游客总数
     */
    Long getVisitorTotal();

    /**
     * 按月统计新增游客趋势（过去12个月）
     */
    List<VisitData> getMonthlyNewVisitors();

    /**
     * 按省份统计游客地域分布
     */
    List<RegionData> getVisitorsByProvince();
}

