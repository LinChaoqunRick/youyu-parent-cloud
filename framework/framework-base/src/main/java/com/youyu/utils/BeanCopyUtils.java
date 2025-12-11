package com.youyu.utils;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;

public class BeanCopyUtils {
    private BeanCopyUtils() {

    }

    public static <T> T copyBean(Object source, Class<T> clazz) {
        // 创建目标对象
        T result = null;
        try {
            result = clazz.newInstance();
            // 实现属性copy
            BeanUtils.copyProperties(source, result);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // 返回结果
        return result;
    }

    public static <T, K> List<T> copyBeanList(List<K> list, Class<T> clazz) {
        return list.stream()
                .map(item -> copyBean(item, clazz))
                .collect(Collectors.toList());
    }
}
