package com.youyu.utils;

public class DateUtils {
    /**
     * 计算两个时间long时间戳相差的天数，不考虑日期前后，返回结果>=0
     *
     * @param before
     * @param after
     * @return
     */
    public static long getAbsTimeStampDiffDay(long before, long after) {
        return (after - before) / (24 * 60 * 60 * 1000);
    }
}
