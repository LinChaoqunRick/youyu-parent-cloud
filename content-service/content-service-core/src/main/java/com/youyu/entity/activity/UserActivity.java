package com.youyu.entity.activity;

import lombok.Data;

import java.util.Date;

@Data
public class UserActivity {
    private Long id;
    private Date createTime;
    private Integer type;
}