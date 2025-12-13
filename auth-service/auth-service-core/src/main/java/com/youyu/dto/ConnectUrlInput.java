package com.youyu.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class ConnectUrlInput {
    @NotBlank(message = "授权类型不能为空")
    public String type;

    public String state = "login";
}
