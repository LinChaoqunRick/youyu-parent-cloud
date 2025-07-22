package com.youyu.result;

import com.youyu.enums.ResultCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ResponseResult<T> {
    private String code = "200";
    private String message = "success";
    private T data;

    public ResponseResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseResult(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(ResultCode.SUCCESS.getCode(), "success", data);
    }

    public static <T> ResponseResult<T> success(String message, T data) {
        return new ResponseResult<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static ResponseResult<?> error(ResultCode resultCode) {
        return new ResponseResult<>(resultCode.getCode(), resultCode.getMessage());
    }

    public static ResponseResult<?> error(String code, String message) {
        return new ResponseResult<>(code, message);
    }

}
