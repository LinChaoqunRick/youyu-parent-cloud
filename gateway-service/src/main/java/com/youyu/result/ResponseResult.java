package com.youyu.result;

import com.youyu.enums.ResultCode;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseResult<T> {
    private String code = "200";
    private String message = "success";
    @Getter
    private T data;


    public ResponseResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseResult<?> error(ResultCode resultCode) {
        return new ResponseResult<>(resultCode.getCode(), resultCode.getMessage());
    }
}
