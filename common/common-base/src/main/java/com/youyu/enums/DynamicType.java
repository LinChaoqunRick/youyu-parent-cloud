package com.youyu.enums;

public enum DynamicType {
    POST(1, "文章"),
    MOMENT(2, "时刻"),
    NOTE(3, "笔记"),
    CHAPTER(4, "笔记章节");

    private Integer code;
    private String desc;

    DynamicType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (DynamicType type : DynamicType.values()) {
            if (code.equals(type.getCode())) {
                return type.desc;
            }
        }
        return null;
    }
}
