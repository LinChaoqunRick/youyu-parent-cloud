package com.youyu.dto.rss;

import lombok.Data;

/**
 * RSS项目信息
 *
 * @author youyu
 */
@Data
public class RssItem {
    /**
     * 项目标题
     */
    private String title;

    /**
     * 项目链接
     */
    private String link;

    /**
     * 项目描述/摘要
     */
    private String description;

    /**
     * 作者
     */
    private String author;

    /**
     * 分类
     */
    private String category;

    /**
     * 发布日期（RFC 822格式）
     */
    private String pubDate;

    /**
     * 全文内容（使用content:encoded）
     */
    private String content;

    /**
     * 唯一标识符
     */
    private String guid;

    /**
     * 封面图片URL
     */
    private String enclosure;

    /**
     * 封面图片类型
     */
    private String enclosureType = "image/jpeg";
}
