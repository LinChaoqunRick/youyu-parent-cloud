package com.youyu.enums;

public enum SMSTemplate {
    COMMON_TEMP(0, "common", "通用", "SMS_202812985"),
    REGISTER_TEMP(1, "register", "注册账号", "SMS_461380436"),
    CHANGE_PASSWORD_TEMP(2, "changePassword", "修改密码", "SMS_461450413"),
    LOGIN_TEMP(3, "login", "登录系统", "SMS_461445378"),
    CHANGE_TELEPHONE_PRE(4, "changeTelephonePrev", "变更手机号_前置", "SMS_461300410"),
    CHANGE_TELEPHONE_NEXT(5, "changeTelephoneNext", "变更手机号_后置", "SMS_461300410"),
    ;
    private Integer id;
    private String label;
    private String name;
    private String code;

    SMSTemplate(Integer id, String label, String name, String code) {
        this.id = id;
        this.label = label;
        this.name = name;
        this.code = code;
    }

    public static String getCodeById(long id) {
        for (SMSTemplate temp : SMSTemplate.values()) {
            if (id == temp.getId()) {
                return temp.code;
            }
        }
        return null;
    }

    public static String getLabelById(long id) {
        for (SMSTemplate temp : SMSTemplate.values()) {
            if (id == temp.getId()) {
                return temp.label;
            }
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
