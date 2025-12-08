package com.youyu.dto.analysis;

import lombok.Data;
import java.util.List;

/**
 * 详细分析数据
 */
@Data
public class AnalysisDetail {
    /**
     * 本年每月访问量
     */
    private List<VisitData> monthlyVisits;

    /**
     * 本月每天访问量
     */
    private List<VisitData> dailyVisits;

    /**
     * 本月区域访问量前10
     */
    private List<RegionData> monthlyRegionVisits;

    /**
     * 本日区域访问量前10
     */
    private List<RegionData> dailyRegionVisits;

    /**
     * 每月评论量（文章评论和时刻评论分开统计）
     */
    private List<CommentData> monthlyComments;

    /**
     * 按月统计的新增游客趋势（独立IP访客数）
     */
    private List<VisitData> monthlyNewVisitors;

    /**
     * 按省份统计游客地域分布（独立IP访客数）
     */
    private List<RegionData> visitorsByProvince;
}