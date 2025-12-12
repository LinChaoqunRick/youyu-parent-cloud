package com.youyu.dto.mail;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentMailSendInput {
    @NotBlank
    String to;
    @NotBlank
    String actorNickname;
    @NotBlank
    String actorToNickname;
    @NotBlank
    String contentType; // 0: 文字 1: 时刻
    @NotBlank
    String subject;
    @NotBlank
    String title;
    @NotBlank
    String commentType;
    @NotBlank
    String content; // 评论内容
    @NotBlank
    String link;
}
