package com.youyu.entity.user;

import lombok.Data;

import java.util.Date;

@Data
public class UserActivities {
    private Long id;
    private Date createTime;
    private Integer type;
}
