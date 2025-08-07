package com.youyu.entity.result;

import lombok.Data;

import java.util.Optional;

@Data
public class TencentLocationResult {
    private int status;
    private String message;
    private String request_id;
    private Result result;

    public int getAdcode() {
        return Optional.ofNullable(result)
                .map(Result::getAd_info)
                .map(AdInfo::getAdcode)
                .orElse(-1);
    }
}


@Data
class Result {
    private String ip;
    private Location location;
    private AdInfo ad_info;

}

@Data
class Location {
    private double lat;
    private double lng;
}

@Data
class AdInfo {
    private String nation;
    private String province;
    private String city;
    private String district;
    private int adcode;
    private int nation_code;
}
