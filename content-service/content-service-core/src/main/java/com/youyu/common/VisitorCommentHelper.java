package com.youyu.common;

import com.youyu.dto.VisitorDTO;
import com.youyu.dto.result.TencentLocationResult;
import com.youyu.enums.ResultCode;
import com.youyu.exception.SystemException;
import com.youyu.feign.UserServiceClient;
import com.youyu.utils.LocateUtils;
import com.youyu.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 游客评论处理助手类
 * 用于统一处理游客评论的身份信息
 *
 * @author youyu
 */
@Component
public class VisitorCommentHelper {

    @Resource
    private UserServiceClient userServiceClient;

    @Resource
    private LocateUtils locateUtils;

    /**
     * 默认头像规格：100x100
     */
    private static final int DEFAULT_AVATAR_SIZE = 100;

    /**
     * Cravatar服务地址（国内访问更快）
     */
    private static final String CRAVATAR_URL = "https://cravatar.cn/avatar/";

    /**
     * 默认头像样式：identicon（几何图案）
     * 可选值：mp, identicon, monsterid, wavatar, retro, robohash, blank
     */
    private static final String DEFAULT_AVATAR_TYPE = "identicon";

    /**
     * 处理评论的身份信息（登录用户或游客）
     *
     * @param comment 评论实体（需要实现VisitorCommentable接口）
     * @return 定位信息结果
     * @throws SystemException 当操作者不能为空时抛出
     */
    public TencentLocationResult processCommentIdentity(VisitorCommentable comment) {
        // 验证操作者不能为空
        if (SecurityUtils.getUserId() == null && comment.getEmail() == null) {
            throw new SystemException(ResultCode.PARAMETER_ERROR.getCode(), "操作者不能为空");
        }

        // 查询IP定位信息
        TencentLocationResult locationResult = locateUtils.queryTencentIp();

        if (SecurityUtils.getUserId() != null) {
            // 用户登录
            comment.setUserId(SecurityUtils.getUserId());
        } else {
            // 游客
            VisitorDTO visitor = new VisitorDTO();
            visitor.setEmail(comment.getEmail());
            visitor.setNickname(comment.getNickname());
            visitor.setHomepage(comment.getHomepage());
            visitor.setAdcode(locationResult.getAdcode());

            // 自动获取头像（优先级：QQ头像 > Gravatar）
            String avatar = getVisitorAvatar(comment.getEmail());
            visitor.setAvatar(avatar);

            visitor = userServiceClient.saveOrUpdateByEmail(visitor).getData();
            comment.setVisitorId(visitor.getId());
        }

        // 设置区域编号
        comment.setAdcode(locationResult.getAdcode());

        return locationResult;
    }

    /**
     * 从QQ邮箱提取QQ头像URL
     *
     * @param email 邮箱地址
     * @return QQ头像URL，如果不是QQ邮箱或格式不正确则返回null
     */
    private String extractQQAvatar(String email) {
        if (email == null || !email.endsWith("@qq.com")) {
            return null;
        }

        String qqNumber = email.substring(0, email.indexOf("@"));

        // 验证QQ号是纯数字
        if (!qqNumber.matches("\\d+")) {
            return null;
        }

        // 返回100x100规格的QQ头像
        return "https://q.qlogo.cn/headimg_dl?dst_uin=" + qqNumber + "&spec=100";
    }

    /**
     * 获取游客头像URL（优先级策略）
     * 优先级：QQ头像 > Gravatar/Cravatar
     *
     * @param email 邮箱地址
     * @return 头像URL
     */
    private String getVisitorAvatar(String email) {
        // 优先尝试QQ头像
        String qqAvatar = extractQQAvatar(email);
        if (qqAvatar != null) {
            return qqAvatar;
        }

        // 使用Gravatar/Cravatar作为备用方案
        return generateGravatarUrl(email);
    }

    /**
     * 生成Gravatar头像URL
     * 使用Cravatar（国内访问更快）
     *
     * @param email 邮箱地址
     * @return Gravatar头像URL
     */
    private String generateGravatarUrl(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }

        // 邮箱转小写并计算MD5
        String emailLowerCase = email.trim().toLowerCase();
        String emailMd5 = md5Hex(emailLowerCase);

        // 构造Cravatar URL
        return CRAVATAR_URL + emailMd5 + "?s=" + DEFAULT_AVATAR_SIZE + "&d=" + DEFAULT_AVATAR_TYPE;
    }

    /**
     * 计算字符串的MD5哈希值（十六进制）
     *
     * @param input 输入字符串
     * @return MD5哈希值（32位十六进制字符串）
     */
    private String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }
}