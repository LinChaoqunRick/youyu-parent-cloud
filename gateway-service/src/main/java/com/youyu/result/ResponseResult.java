package com.youyu.result;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseResult<T> {
    private int code = 200;
    private String message = "success";
    @Getter
    private T data;

    public ResponseResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseResult<T> error(int code, String message) {
        return new ResponseResult<T>(code, message, null);
    }

}
