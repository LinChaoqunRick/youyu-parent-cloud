package com.youyu.entity.user;

import lombok.Getter;
import lombok.Setter;

/**
 * 操作者基础信息：用户 or 游客
 */
@Getter
@Setter
public class Actor {
    private Long id;
    private String nickname;
    private String avatar;
    private Integer sex;
    private Integer level;
    private Integer adcode;
    private String adname;
    private String signature;
    private boolean follow; // 是否关注了
    private int type = 0; // 0: 用户 1: 游客
}
