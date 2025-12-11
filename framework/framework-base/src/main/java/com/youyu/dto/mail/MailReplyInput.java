package com.youyu.dto.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailReplyInput {
    String target;
    String nickname;
    String caption;
    String subject;
    String content;
    String url;
}
