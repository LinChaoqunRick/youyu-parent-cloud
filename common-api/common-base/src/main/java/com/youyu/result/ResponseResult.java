package com.youyu.result;

import com.youyu.enums.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseResult<T> {
    private int code = 200;
    private String message = "success";
    private T data;

    public ResponseResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseResult(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(ResultCode.SUCCESS.getCode(), "success", data);
    }

    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static ResponseResult error(ResultCode resultCode) {
        return new ResponseResult(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public static <T> ResponseResult<T> error(int code, String message) {
        return new ResponseResult<T>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
