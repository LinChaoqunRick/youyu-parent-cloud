package com.youyu.utils;

public class NumberUtils {

    public static String createRandomNumber(int length) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int x = 0; x < length; x++) {
            int random = (int) (Math.random() * (10 - 1));
            stringBuffer.append(random);
        }
        return stringBuffer.toString();
    }
}
