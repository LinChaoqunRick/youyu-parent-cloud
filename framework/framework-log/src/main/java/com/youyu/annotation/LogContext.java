package com.youyu.annotation;

import java.util.Map;
import java.util.HashMap;

public class LogContext {
    private static final ThreadLocal<Map<String, Object>> CONTEXT = ThreadLocal.withInitial(HashMap::new);

    public static void put(String key, Object value) {
        CONTEXT.get().put(key, value);
    }

    public static Object get(String key) {
        return CONTEXT.get().get(key);
    }

    public static Map<String, Object> getAll() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
