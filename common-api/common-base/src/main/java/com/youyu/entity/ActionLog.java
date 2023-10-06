package com.youyu.entity;

import lombok.Data;

@Data
public class ActionLog {
    private Long id;
    private Long userId;
    private String ip;
    private String action;
    private String path;
    private Long duration;
}
