package com.youyu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    /**
     * 用户名、手机号码
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像e
     */
    private String avatar;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 地址编号
     */
    private Integer adcode;
    /**
     * 地址简称
     */
    private String adname;
    /**
     * 个人主页
     */
    private String homepage;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 个性签名
     */
    private String signature;
}
