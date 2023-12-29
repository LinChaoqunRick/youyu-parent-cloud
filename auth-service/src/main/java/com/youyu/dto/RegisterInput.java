package com.youyu.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterInput {
    @NotBlank(message = "昵称不能为空")
    public String nickname;
    public String username;
    public String email;
    @NotBlank(message = "密码不能为空")
    public String password;
    @NotBlank(message = "验证码不能为空")
    public String code;
    @NotBlank(message = "类型不能为空")
    public int type;
}
