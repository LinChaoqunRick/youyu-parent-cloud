package com.youyu.utils;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        try {
            T entity = clazz.newInstance();
            BeanUtils.populate(entity, map);
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T requestParamsMapToBean(Map<String, String[]> map, Class<T> entityClass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T entity = entityClass.newInstance();

        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String propertyName = entry.getKey();
            String[] propertyValueArray = entry.getValue();
            if (propertyValueArray != null && propertyValueArray.length > 0) {
                String propertyValue = propertyValueArray[0];
                BeanUtils.setProperty(entity, propertyName, propertyValue);
            }
        }

        return entity;
    }
}
