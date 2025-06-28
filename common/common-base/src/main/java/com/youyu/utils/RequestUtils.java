package com.youyu.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Component
public class RequestUtils {

    private static boolean isInvalid(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取用户真实IP
     *
     * @return 调用者IP
     */
    public static String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = null;

        // 优先使用 X-Real-IP（如果你在网关或代理层设置了）
        ipAddress = request.getHeader("X-Real-IP");
        if (isInvalid(ipAddress)) {
            ipAddress = request.getHeader("X-Forwarded-For");
        }
        if (isInvalid(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalid(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isInvalid(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // 多个 IP 时，取第一个
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        // IPv6 本地地址转换
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
//            ipAddress = "127.0.0.1";
            ipAddress = "110.87.98.58"; // 模拟一下
        }

        return ipAddress;
    }

    /**
     * 获取请求的HttpServletRequest对象
     *
     * @return HttpServletRequest对象
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            return null;
        } else {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
    }
}
