package com.youyu.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class PositionInfo {
    /**
     * 若为直辖市则显示直辖市名称；
     * 如果在局域网 IP网段内，则返回“局域网”；
     * 非法IP以及国外IP则返回空
     */
    private Object province;

    /**
     * 若为直辖市则显示直辖市名称；
     * 如果为局域网网段内IP或者非法IP或国外IP，则返回空
     */
    private Object city;
    /**
     * 城市的adcode编码
     */
    private Object adcode;
    /**
     * 所在城市矩形区域范围
     * 所在城市范围的左下右上对标对
     */
    private Object rectangle;

    public String getProvince() {
        if (province instanceof String) {
            return (String) province;
        } else if (province instanceof List) {
            // 这里处理数组/列表的情况，例如返回空字符串或特定标志
            return null; // 或者其他逻辑
        }
        return null; // 或抛出一个异常，根据你的业务逻辑决定
    }

    public String getCity() {
        if (city instanceof String) {
            return (String) city;
        } else if (city instanceof List) {
            // 这里处理数组/列表的情况，例如返回空字符串或特定标志
            return null; // 或者其他逻辑
        }
        return null; // 或抛出一个异常，根据你的业务逻辑决定
    }

    public Integer getAdcode() {
        Class<?> clazz = adcode.getClass();
        log.info(clazz.getName());
        if (adcode instanceof String) {
            return Integer.parseInt(adcode.toString());
        } else if (adcode instanceof Integer) {
            return (Integer) adcode;
        } else if (adcode instanceof List) {
            // 这里处理数组/列表的情况，例如返回空字符串或特定标志
            return null; // 或者其他逻辑
        }
        return null; // 或抛出一个异常，根据你的业务逻辑决定
    }

    public String getRectangle() {
        if (rectangle instanceof String) {
            return (String) rectangle;
        } else if (rectangle instanceof List) {
            // 这里处理数组/列表的情况，例如返回空字符串或特定标志
            return ""; // 或者其他逻辑
        }
        return null; // 或抛出一个异常，根据你的业务逻辑决定
    }
}
