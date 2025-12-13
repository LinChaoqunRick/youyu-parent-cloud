package com.youyu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitorDTO {
    //主键
    private Long id;
    //昵称
    private String nickname;
    //头像
    private String avatar;
    //邮箱
    private String email;
    //主页
    private String homepage;
    //区域编码
    private Integer adcode;
}
