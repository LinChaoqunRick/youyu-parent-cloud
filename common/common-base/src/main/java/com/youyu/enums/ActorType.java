package com.youyu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActorType {
    USER(0, "用户"),
    VISITOR(1, "游客");

    private final int code;
    private final String desc;
}
