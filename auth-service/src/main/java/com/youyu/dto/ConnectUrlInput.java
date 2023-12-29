package com.youyu.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ConnectUrlInput {
    @NotBlank(message = "授权类型不能为空")
    public String type;
}
