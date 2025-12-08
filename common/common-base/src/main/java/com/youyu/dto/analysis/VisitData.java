package com.youyu.dto.analysis;

import lombok.Data;
import java.util.List;

/**
 * 访问数据（可用于月份或日期）
 */
@Data
public class VisitData {
    /**
     * 时间标识（月份格式：2024-01 或 日期格式：2024-01-15）
     */
    private String time;

    /**
     * 访问量
     */
    private Long visitCount;
}