package com.youyu.dto.post.post;

import lombok.Data;

@Data
public class PostUserOutput {
    private Long id;
    private String nickname;
    private String avatar;
    private String email;
    private Integer sex;
    private Integer level;
    private String signature;
    private String status;
    private PostUserExtraInfo extraInfo;
    private boolean follow;
}
