package com.youyu.utils;

public class StringUtils {

    public static String ellipsisUnicode(String text, int maxLength) {
        return ellipsisUnicode(text, maxLength, "...");
    }

    public static String ellipsisUnicode(String text, int maxLength, String suffix) {
        if (text == null) {
            return null;
        }
        if (maxLength <= 0) {
            return "";
        }

        int actualLength = text.codePointCount(0, text.length());
        if (actualLength <= maxLength) {
            return text;
        }

        // 找到 code point 对应的 char 下标
        int endIndex = text.offsetByCodePoints(0, maxLength);

        return text.substring(0, endIndex) + suffix;
    }
}
