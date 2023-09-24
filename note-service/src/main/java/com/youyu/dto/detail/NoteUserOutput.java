package com.youyu.dto.detail;

import lombok.Data;

@Data
public class NoteUserOutput {
    private Long id;
    private String nickname;
    private String avatar;
    private Integer sex;
    private Integer level;
    private String signature;
    private String status;
    private boolean follow;
    private NoteUserExtraInfo extraInfo;
}
