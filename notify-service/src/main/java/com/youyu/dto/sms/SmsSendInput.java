package com.youyu.dto.sms;

import com.youyu.enums.SMSTemplate;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class SmsSendInput {
    @NotBlank(message = "手机号不能为空")
    String telephone;
    boolean repeat = false;
    Integer type = SMSTemplate.COMMON_TEMP.getId();
}
