package com.youyu.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanTransformUtils {

    public static Map<String, Object> beanToMap(Object object) {
        Map<String, Object> dataMap = new HashMap<>();
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                dataMap.put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return dataMap;
    }
}
