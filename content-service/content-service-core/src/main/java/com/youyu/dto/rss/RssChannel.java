package com.youyu.dto.rss;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * RSS频道信息
 *
 * @author youyu
 */
@Data
public class RssChannel {
    /**
     * 频道标题
     */
    private String title;

    /**
     * 频道链接
     */
    private String link;

    /**
     * 频道描述
     */
    private String description;

    /**
     * 频道语言
     */
    private String language = "zh-CN";

    /**
     * 最后构建时间
     */
    private String lastBuildDate;

    /**
     * 生成器
     */
    private String generator = "YouYu RSS Generator";

    /**
     * RSS项目列表
     */
    private List<RssItem> items = new ArrayList<>();

    public RssChannel(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }
}
