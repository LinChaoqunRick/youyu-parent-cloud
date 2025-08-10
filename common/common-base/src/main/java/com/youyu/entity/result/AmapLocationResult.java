package com.youyu.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class AmapLocationResult {
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
     * 城市的adname, 此处自己转化
     */
    private String adname;
    /**
     * 所在城市矩形区域范围
     * 所在城市范围的左下右上对标对
     */
    private Object rectangle;
}
