package com.youyu.common;

/**
 * 游客评论接口
 * 用于统一处理需要支持游客评论的实体类
 *
 * @author youyu
 */
public interface VisitorCommentable {
    /**
     * 获取用户ID
     */
    Long getUserId();

    /**
     * 设置用户ID
     */
    void setUserId(Long userId);

    /**
     * 获取游客ID
     */
    Long getVisitorId();

    /**
     * 设置游客ID
     */
    void setVisitorId(Long visitorId);

    /**
     * 获取区域编号
     */
    Integer getAdcode();

    /**
     * 设置区域编号
     */
    void setAdcode(Integer adcode);

    /**
     * 获取游客邮箱
     */
    String getEmail();

    /**
     * 设置游客邮箱
     */
    void setEmail(String email);

    /**
     * 获取游客昵称
     */
    String getNickname();

    /**
     * 设置游客昵称
     */
    void setNickname(String nickname);

    /**
     * 获取游客主页
     */
    String getHomepage();

    /**
     * 设置游客主页
     */
    void setHomepage(String homepage);
}