package com.youyu.annotation;

import com.youyu.enums.LogType;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log
{
    /**
     * 模块
     */
    String title() default "";

    /**
     * 功能
     */
    LogType type() default LogType.OTHER;

    /**
     * 是否保存请求的参数
     */
    boolean savaRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    boolean saveResponseData() default false;

    /**
     * 排除指定的请求参数
     */
    String[] excludeParamNames() default {};
}
