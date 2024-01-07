package com.youyu.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConnectBindInput {
    @NotBlank(message = "类型不能为空")
    private String type;
    @NotBlank(message = "code不能为空")
    private String code;
}
