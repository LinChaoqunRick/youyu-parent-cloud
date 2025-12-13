package com.youyu.dto.moment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MomentUserOutput {
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
    private MomentUserExtraInfo extraInfo;
}
