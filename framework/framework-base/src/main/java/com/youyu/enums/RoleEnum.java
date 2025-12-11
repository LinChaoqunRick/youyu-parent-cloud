package com.youyu.enums;

public enum RoleEnum {
    AUTHOR(1, "作者"),
    GENERAL_USER(2, "普通用户"),
    NO_LOGGED_USER(3, "未登录"),
    ;
    private long id;
    private String name;

    RoleEnum(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
