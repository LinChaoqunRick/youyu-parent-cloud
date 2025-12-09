package com.youyu.dto.message;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMessageInput {
    private Long rootId;
    private Long userId;
    private Long visitorId;
    private String nickname;
    private String email;
    private String avatar;
    private String homepage;
    @NotBlank(message = "内容不能为空")
    private String content;
}
