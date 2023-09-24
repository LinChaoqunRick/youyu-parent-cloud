package com.youyu.enums;

public enum PostType {
    Post("0", "文章"),
    Draft("0", "草稿");

    private String code;
    private String desc;

    PostType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getDescByCode(String code) {
        for (PostType type : PostType.values()) {
            if (code.equals(type.getCode())) {
                return type.desc;
            }
        }
        return null;
    }
}
