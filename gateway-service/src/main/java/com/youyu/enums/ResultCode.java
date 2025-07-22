package com.youyu.enums;

import lombok.Getter;
import org.apache.http.HttpStatus;

@Getter
public enum ResultCode implements HttpStatus {
    SUCCESS("200", "成功"),
    TOKEN_INVALID("A0001", "token无效或已过期"),
    TOKEN_ACCESS_FORBIDDEN("A0002", "token已被禁止访问"),
    ACCESS_UNAUTHORIZED("A0003", "访问未授权"),
    ;

    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
