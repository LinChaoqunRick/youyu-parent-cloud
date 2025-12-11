package com.youyu.dto.mail;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentMailSendInput {
    @NotBlank
    String to;
    @NotBlank
    String actorNickname;
    @NotBlank
    String actorToNickName;
    @NotBlank
    String title;
    @NotBlank
    int commentType; // 0: 评论 1: 回复
    @NotBlank
    String content; // 评论内容
    @NotBlank
    String link;
}
