package com.youyu.enums;

import lombok.Getter;

@Getter
public enum LogType {
    INSERT(1, "插入日志"),
    UPDATE(2, "修改日志"),
    QUERY(3, "查询日志"),
    DELETE(4, "删除日志"),
    LOGIN(5, "登录日志"),
    LOGOUT(6, "登出日志"),
    REGISTER(7, "注册日志"),
    ACCESS(8, "访问网站"),
    NOTIFY_SMS(9, "通知_短信"),
    NOTIFY_MAIL(10, "通知_邮件"),
    UPLOAD(20, "文件上传"),
    OTHER(66, "其他"),
    ;

    private final int code;
    private final String desc;

    LogType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}