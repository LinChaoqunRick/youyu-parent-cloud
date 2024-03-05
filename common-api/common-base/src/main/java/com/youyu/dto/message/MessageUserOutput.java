package com.youyu.dto.message;

import lombok.Data;

@Data
public class MessageUserOutput {
    private Long id;
    private String nickname;
    private String avatar;
    private Integer sex;
    private Integer level;
    private String signature;
    private boolean follow;
}
