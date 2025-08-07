package com.youyu.utils;

import com.youyu.entity.result.AmapLocationResult;
import com.youyu.entity.result.TencentLocationResult;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


@Component
public class LocateUtils {

    @Resource
    private RestTemplate restTemplate;

    @Value("${amap.key}")
    private String amapKey;

    @Value("${tencent.key}")
    private String tencentKey;

    /**
     * 高德地图
     * @return ip地址信息
     */
    public AmapLocationResult queryAmapIp() {
        String clientIp = RequestUtils.getClientIp();
        return restTemplate.getForObject("https://restapi.amap.com/v3/ip?key=" + amapKey + "&ip=" + clientIp, AmapLocationResult.class);
    }

    /**
     * 腾讯地图
     * @return ip地址信息
     */
    public TencentLocationResult queryTencentIp() {
        String clientIp = RequestUtils.getClientIp();
        return restTemplate.getForObject("https://apis.map.qq.com/ws/location/v1/ip?key=" + tencentKey + "&ip=" + clientIp, TencentLocationResult.class);
    }

    /**
     * ip138
     */
    public static String DATATYPE = "json";

    public static String get(String urlString, String token) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(5 * 1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token", token);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder builder = new StringBuilder();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"));
                for (String s = br.readLine(); s != null; s = br
                        .readLine()) {
                    builder.append(s);
                }
                br.close();
                return builder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String queryIP(String ip) {
        String url = "https://api.ip138.com/ipdata/?ip=" + ip + "&datatype=" + DATATYPE;
        String token = "a9890c8a06ce99062045b2b644a9dc9b";
        return get(url, token);
    }

    public static void main(String[] args) {
        String data = queryIP("110.87.98.58");
    }
}
