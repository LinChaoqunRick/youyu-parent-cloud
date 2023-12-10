package com.youyu.enums;

public enum AdCode {
    北京市("北京市", 110000),
    天津市("天津市", 120000),
    河北省("河北省", 130000),
    山西省("山西省", 140000),
    内蒙古自治区("内蒙古自治区", 150000),
    辽宁省("辽宁省", 210000),
    吉林省("吉林省", 220000),
    黑龙江省("黑龙江省", 230000),
    上海市("上海市", 310000),
    江苏省("江苏省", 320000),
    浙江省("浙江省", 330000),
    安徽省("安徽省", 340000),
    福建省("福建省", 350000),
    江西省("江西省", 360000),
    山东省("山东省", 370000),
    河南省("河南省", 410000),
    湖北省("湖北省", 420000),
    湖南省("湖南省", 430000),
    广东省("广东省", 440000),
    广西壮族自治区("广西壮族自治区", 450000),
    海南省("海南省", 460000),
    重庆市("重庆市", 500000),
    四川省("四川省", 510000),
    贵州省("贵州省", 520000),
    云南省("云南省", 530000),
    西藏自治区("西藏自治区", 540000),
    陕西省("陕西省", 610000),
    甘肃省("甘肃省", 620000),
    青海省("青海省", 630000),
    宁夏回族自治区("宁夏回族自治区", 640000),
    新疆维吾尔自治区("新疆维吾尔自治区", 650000),
    香港特别行政区("香港特别行政区", 810000),
    澳门特别行政区("澳门特别行政区", 820000),
    ;
    private String name;
    private int adcode;

    AdCode(String name, int adcode) {
        this.name = name;
        this.adcode = adcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAdcode() {
        return adcode;
    }

    public void setAdcode(int adcode) {
        this.adcode = adcode;
    }

    public static String getDescByCode(Integer adcode) {
        for (AdCode type : AdCode.values()) {
            if (adcode.equals(type.getAdcode())) {
                return type.getName();
            }
        }
        return null;
    }

}
