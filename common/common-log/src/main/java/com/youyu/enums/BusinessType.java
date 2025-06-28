package com.youyu.enums;

import lombok.Getter;

@Getter
public enum BusinessType {
    INSERT(1),
    UPDATE(2),
    QUERY(3),
    DELETE(4),
    LOGIN(5),
    LOGOUT(6),
    REGISTER(7),
    GET_ROUTER(10),
    OTHER(66),
    ;

    private final int code;

    BusinessType(int code) {
        this.code = code;
    }
}