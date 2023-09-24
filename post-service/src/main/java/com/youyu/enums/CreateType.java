package com.youyu.enums;

public enum CreateType {
    ORIGINAL("0", "原创"),
    REPRINT("1", "转载"),
    TRANSLATE("2", "翻译");

    private String code;
    private String desc;

    CreateType(String code, String desc) {
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
        for (CreateType type : CreateType.values()) {
            if (code.equals(type.getCode())) {
                return type.desc;
            }
        }
        return null;
    }

}
