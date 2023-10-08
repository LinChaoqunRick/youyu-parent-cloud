package com.youyu.enums;

public enum ResultCode {
    SUCCESS(200, "成功"),
    INTERNAL_SERVER_ERROR(500, "内部服务器错误"),
    NEED_LOGIN(401, "需要登录后操作401"),
    NO_OPERATOR_AUTH(403, "无权限操作，拒绝访问"),
    NOT_FOUND(404, "访问的资源不存在"),
    LOGIN_ERROR(505, "用户名或密码错误222"),
    REQUIRE_USERNAME(504, "用户名不能为空"),
    EMAIL_CONFLICT(505, "邮箱已存在"),
    NICKNAME_CONFLICT(506, "昵称已存在"),
    TELEPHONE_CONFLICT(507, "用户已存在"),
    INVALID_METHOD_ARGUMENT(508, "非法的参数错误"),
    CONTENT_NOT_EXIST(509, "内容不存在"),
    OPERATION_FAIL(510, "操作失败"),
    USER_NOT_EXIST(512, "用户不存在"),
    CODE_INCORRECT(514, "验证码错误"),
    MESSAGE_SEND_FAILED(600, "短信发送失败"),
    ;

    private int code;
    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
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
}
