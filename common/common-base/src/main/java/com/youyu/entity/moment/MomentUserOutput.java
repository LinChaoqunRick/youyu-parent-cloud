package com.youyu.entity.moment;

import lombok.Data;

@Data
public class MomentUserOutput {
    private Long id;
    private String nickname;
    private String avatar;
    private Integer sex;
    private Integer level;
    private String signature;
    private MomentUserExtraInfo extraInfo;
    private boolean follow;
}
