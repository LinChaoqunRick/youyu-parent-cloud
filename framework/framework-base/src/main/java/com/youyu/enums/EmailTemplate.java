package com.youyu.enums;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    REGISTER_MAIL(0, "注册验证码邮件", "111111"),
    COMMENT_MAIL(10, "评论回复邮件", "418136"),
    ;
    private final Integer id;
    private final String name;
    private final String code;

    EmailTemplate(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public static String getCodeById(long id) {
        for (EmailTemplate temp : EmailTemplate.values()) {
            if (id == temp.getId()) {
                return temp.code;
            }
        }
        return null;
    }
}
