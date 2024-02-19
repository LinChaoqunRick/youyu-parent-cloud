package com.youyu.dto;

import com.youyu.enums.SMSTemplate;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;

@Data
public class SmsVerifyInput {
    @NotBlank(message = "手机号不能为空")
    String telephone;
    @NotBlank(message = "验证码不能为空")
    String code;
    Integer type = SMSTemplate.COMMON_TEMP.getId();
}
