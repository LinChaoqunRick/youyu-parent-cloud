package com.youyu.dto;

import lombok.Data;

/**
 * 区域访问数据
 */
@Data
public class RegionData {
    /**
     * 区域代码
     */
    private Integer adcode;

    /**
     * 区域名称（省份）
     */
    private String adName;

    /**
     * 访问量
     */
    private Long visitCount;
}