package com.youyu.constant;

/**
 * JWT声明常量
 *
 * @author Ray.Hao
 * @since 1.0.0
 */
public interface JwtClaimConstants {

    /**
     * 用户ID
     */
    String USER_ID = "id";

    /**
     * 用户名
     */
    String USERNAME = "username";

    /**
     * 数据权限
     */
    String DATA_SCOPE = "dataScope";

    /**
     * 权限(角色Code)集合
     */
    String AUTHORITIES = "authorities";
}
