package com.youyu.enums;

public enum AdCode {
    Beijing("北京", "北京市", 110000),
    Tianjin("天津", "天津市", 120000),
    Hebei("河北", "河北省", 130000),
    Shanxi("山西", "山西省", 140000),
    Neimenggu("内蒙古", "内蒙古自治区", 150000),
    Liaoning("辽宁", "辽宁省", 210000),
    Jilin("吉林", "吉林省", 220000),
    Heilongjiang("黑龙江", "黑龙江省", 230000),
    Shanghai("上海", "上海市", 310000),
    Jiangsu("江苏", "江苏省", 320000),
    Zhejiang("浙江", "浙江省", 330000),
    Anhui("安徽", "安徽省", 340000),
    Fujian("福建", "福建省", 350000),
    Jiangxi("江西", "江西省", 360000),
    Shandong("山东", "山东省", 370000),
    Henan("河南", "河南省", 410000),
    Hubei("湖北", "湖北省", 420000),
    Hunan("湖南", "湖南省", 430000),
    Guangdong("广东", "广东省", 440000),
    Guangxi("广西", "广西壮族自治区", 450000),
    Hainan("海南", "海南省", 460000),
    Chongqing("重庆", "重庆市", 500000),
    Sichuan("四川", "四川省", 510000),
    Guizhou("贵州", "贵州省", 520000),
    Yunnan("云南", "云南省", 530000),
    Xizang("西藏", "西藏自治区", 540000),
    Shaanxi("陕西", "陕西省", 610000),
    Gansu("甘肃", "甘肃省", 620000),
    Qinghai("青海", "青海省", 630000),
    Ningxia("宁夏", "宁夏回族自治区", 640000),
    Xinjiang("新疆", "新疆维吾尔自治区", 650000),
    Xianggang("香港", "香港特别行政区", 810000),
    Aomen("澳门", "澳门特别行政区", 820000),
    ;
    private String name;
    private String fullName;
    private Integer adcode;

    AdCode(String name, String fullName, int adcode) {
        this.name = name;
        this.fullName = fullName;
        this.adcode = adcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAdcode() {
        return adcode;
    }

    public void setAdcode(int adcode) {
        this.adcode = adcode;
    }

    public static String getDescByCode(Integer adcode) {
        if (adcode == null) {
            return null;
        }
        String areaName = null;
        for (AdCode type : AdCode.values()) {
            if (type.getAdcode() <= adcode) {
                areaName = type.getName();
            } else {
                return areaName;
            }
        }
        return areaName;
    }

    public static void main(String[] args) {
        System.out.println(getDescByCode(370200));
    }
}
