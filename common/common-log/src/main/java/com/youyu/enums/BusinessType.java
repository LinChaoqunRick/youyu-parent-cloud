package com.youyu.enums;

import lombok.Getter;

@Getter
public enum BusinessType {
    INSERT(1, "插入日志"),
    UPDATE(2, "修改日志"),
    QUERY(3, "查询日志"),
    DELETE(4, "删除日志"),
    LOGIN(5, "登录日志"),
    LOGOUT(6, "登出日志"),
    REGISTER(7, "注册日志"),
    OTHER(66, "其他"),
    ;

    private final int code;
    private final String desc;

    BusinessType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}