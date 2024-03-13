package com.youyu.enums;

import lombok.Getter;
import org.apache.http.HttpStatus;

@Getter
public enum ResultCode implements HttpStatus {
    SUCCESS(200, "成功"),
    INTERNAL_SERVER_ERROR(500, "内部服务器错误"),
    UNAUTHORIZED(401, "未认证用户"),
    FORBIDDEN(403, "拒绝访问"),
    NOT_FOUND(404, "访问的资源不存在"),
    LOGIN_ERROR(505, "用户名或密码错误"),
    REQUIRE_USERNAME(504, "用户名不能为空"),
    EMAIL_CONFLICT(505, "邮箱已存在"),
    NICKNAME_CONFLICT(506, "昵称已存在"),
    TELEPHONE_CONFLICT(507, "用户已存在"),
    INVALID_METHOD_ARGUMENT(508, "非法的参数错误"),
    CONTENT_NOT_EXIST(509, "内容不存在"),
    OPERATION_FAIL(510, "操作失败"),
    USER_NOT_EXIST(512, "用户不存在"),
    CODE_INCORRECT(514, "验证码错误"),
    OTHER_ERROR(530, "其它需要展示弹窗的错误"),
    MESSAGE_SEND_FAILED(600, "短信发送失败"),
    ;

    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
