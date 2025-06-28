package com.youyu.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BeanUtils {
    /**
     * 根据属性名获取属性值 https://www.cnblogs.com/keephub/p/15766732.html
     *
     * @param obj
     * @param propertyName
     * @return
     */
    public static Object getFieldValueByFieldName(Object obj, String propertyName) {
        String getMethodName = "get"
                + propertyName.substring(0, 1).toUpperCase()
                + propertyName.substring(1);

        Class c = obj.getClass();
        try {
            Method m = c.getMethod(getMethodName);
            Object value = m.invoke(obj);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 根据属性名获取属性元素，包括各种安全范围和所有父类 https://www.cnblogs.com/keephub/p/15766732.html
     *
     * @param fieldName
     * @param object
     * @return
     */
    public static Field getFieldByClass(Object object, String fieldName) {
        Field field = null;
        Class<?> clazz = object.getClass();

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
                // 这里甚么都不能抛出去。
                // 如果这里的异常打印或者往外抛，则就不会进入
            }
        }
        return field;

    }
}
