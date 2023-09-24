package com.youyu.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DynamicInfo {
    private Long id;
    private Date createTime;
    private Integer type;
}
