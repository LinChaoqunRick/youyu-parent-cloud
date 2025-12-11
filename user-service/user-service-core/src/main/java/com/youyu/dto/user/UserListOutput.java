package com.youyu.dto.user;

import lombok.Data;

@Data
public class UserListOutput {
    private Long id;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 性别
     */
    private Integer sex;
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
    /**
     * 当前用户是否已关注
     */
    private boolean follow;
}
