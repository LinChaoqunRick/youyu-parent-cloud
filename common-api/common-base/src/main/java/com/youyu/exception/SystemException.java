package com.youyu.exception;

import com.youyu.enums.ResultCode;

public class SystemException extends RuntimeException {
    private final int code;
    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public SystemException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public SystemException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
