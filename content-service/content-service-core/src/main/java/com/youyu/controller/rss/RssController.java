package com.youyu.controller.rss;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyu.dto.rss.RssChannel;
import com.youyu.dto.rss.RssItem;
import com.youyu.entity.moment.Moment;
import com.youyu.entity.post.Post;
import com.youyu.service.moment.MomentService;
import com.youyu.service.post.PostService;
import com.youyu.service.rss.RssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.Date;

/**
 * RSS订阅控制器
 *
 * @author youyu
 */
@RestController
@RequestMapping("/rss/open")
@Slf4j
public class RssController {

    @Resource
    private PostService postService;

    @Resource
    private MomentService momentService;

    @Resource
    private RssService rssService;

    /**
     * 网站URL配置（可通过配置文件设置）
     */
    @Value("${youyu.site.url:https://v2.youyu.com}")
    private String siteUrl;

    /**
     * 网站名称配置
     */
    @Value("${youyu.site.name:有语}")
    private String siteName;

    /**
     * 统一RSS订阅（包含文章和时刻）
     *
     * @param limit 返回内容数量，默认20条
     * @return RSS XML格式内容
     */
    @GetMapping(value = "/rss.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String rssFeed(@RequestParam(defaultValue = "20") Integer limit) {
        log.info("访问统一RSS订阅，limit={}", limit);

        int maxLimit = Math.min(limit, 100); // 最多100条

        // 查询最新发布的文章
        LambdaQueryWrapper<Post> postQueryWrapper = new LambdaQueryWrapper<>();
        postQueryWrapper.eq(Post::getStatus, 0) // 只查询已发布的文章
                .eq(Post::getDeleted, 0) // 排除已删除的
                .orderByDesc(Post::getCreateTime);
        Page<Post> postPage = new Page<>(1, maxLimit);
        postPage = postService.page(postPage, postQueryWrapper);

        // 查询最新时刻
        LambdaQueryWrapper<Moment> momentQueryWrapper = new LambdaQueryWrapper<>();
        momentQueryWrapper.eq(Moment::getDeleted, 0)
                .orderByDesc(Moment::getCreateTime);
        Page<Moment> momentPage = new Page<>(1, maxLimit);
        momentPage = momentService.page(momentPage, momentQueryWrapper);

        // 构建RSS Channel
        RssChannel channel = new RssChannel(
                siteName + " - 全站订阅",
                siteUrl + "/rss/rss.xml",
                siteName + "网站的所有内容更新订阅"
        );

        // 创建一个包含时间戳的临时列表用于排序
        java.util.List<RssItemWithTime> itemsWithTime = new java.util.ArrayList<>();

        // 转换文章为RSS Items
        for (Post post : postPage.getRecords()) {
            RssItem item = new RssItem();
            item.setTitle(post.getTitle());
            item.setLink(siteUrl + "/post/detail/" + post.getId());
            item.setGuid("post-" + post.getId());
            item.setDescription(post.getSummary());
            item.setContent(post.getContent());
            item.setPubDate(rssService.formatRFC822Date(post.getCreateTime()));

            // 设置封面图片
            if (post.getThumbnail() != null && !post.getThumbnail().isEmpty()) {
                item.setEnclosure(post.getThumbnail());
            }

            itemsWithTime.add(new RssItemWithTime(item, post.getCreateTime()));
        }

        // 转换时刻为RSS Items
        for (Moment moment : momentPage.getRecords()) {
            RssItem item = new RssItem();

            // 时刻标题使用内容前50个字符
            String title = rssService.truncateSummary(moment.getContent(), 50);
            item.setTitle(title);
            item.setLink(siteUrl + "/moment/detail/" + moment.getId());
            item.setGuid("moment-" + moment.getId());
            item.setDescription(moment.getContent());
            item.setContent(buildMomentContent(moment));
            item.setPubDate(rssService.formatRFC822Date(moment.getCreateTime()));

            // 如果有图片，使用第一张作为封面
            if (moment.getImages() != null && !moment.getImages().isEmpty()) {
                String[] images = moment.getImages().split(",");
                if (images.length > 0) {
                    item.setEnclosure(images[0]);
                }
            }

            itemsWithTime.add(new RssItemWithTime(item, moment.getCreateTime()));
        }

        // 按时间倒序排序（最新的在前）
        itemsWithTime.sort((a, b) -> b.getTime().compareTo(a.getTime()));

        // 取前limit条
        itemsWithTime.stream()
                .limit(maxLimit)
                .forEach(itemWithTime -> channel.getItems().add(itemWithTime.getItem()));

        return rssService.generateRssXml(channel);
    }

    /**
     * 获取RSS订阅列表（帮助页面）
     *
     * @return RSS订阅地址列表
     */
    @GetMapping(value = "/index", produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "<!DOCTYPE html>" +
                "<html lang=\"zh-CN\">" +
                "<head>" +
                "  <meta charset=\"UTF-8\">" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "  <title>" + siteName + " - RSS订阅</title>" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; line-height: 1.6; }" +
                "    h1 { color: #333; }" +
                "    h2 { color: #666; margin-top: 30px; }" +
                "    ul { list-style: none; padding: 0; }" +
                "    li { margin: 10px 0; padding: 10px; background: #f5f5f5; border-radius: 5px; }" +
                "    a { color: #0066cc; text-decoration: none; }" +
                "    a:hover { text-decoration: underline; }" +
                "    .rss-url { background: #e8f4f8; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #0066cc; }" +
                "    .rss-url strong { font-size: 18px; color: #0066cc; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <h1>" + siteName + " RSS订阅中心</h1>" +
                "  <p>欢迎订阅" + siteName + "的内容更新！你可以使用RSS阅读器订阅网站的所有内容，包括文章和动态时刻。</p>" +
                "  <div class=\"rss-url\">" +
                "    <strong>📡 全站订阅</strong><br/>" +
                "    <a href=\"" + siteUrl + "/rss/rss.xml\">" + siteUrl + "/rss/rss.xml</a><br/>" +
                "    <small>订阅网站所有最新内容（文章 + 时刻），按时间混合排序</small>" +
                "  </div>" +
                "  <h2>📖 如何使用RSS？</h2>" +
                "  <p>RSS是一种内容聚合技术，可以让你在一个地方订阅多个网站的更新。推荐的RSS阅读器：</p>" +
                "  <ul>" +
                "    <li><strong>桌面端：</strong>Feedly、Inoreader、NewsBlur</li>" +
                "    <li><strong>移动端：</strong>Reeder、NetNewsWire、Feedly</li>" +
                "    <li><strong>浏览器插件：</strong>RSS Feed Reader</li>" +
                "  </ul>" +
                "  <p>只需将上面的RSS链接添加到你的RSS阅读器中即可开始订阅！</p>" +
                "</body>" +
                "</html>";
    }

    /**
     * 构建时刻的完整内容（包含图片）
     */
    private String buildMomentContent(Moment moment) {
        StringBuilder content = new StringBuilder();
        content.append("<p>").append(moment.getContent()).append("</p>");

        // 添加图片
        if (moment.getImages() != null && !moment.getImages().isEmpty()) {
            String[] images = moment.getImages().split(",");
            content.append("<div class=\"moment-images\">");
            for (String image : images) {
                content.append("<img src=\"").append(image).append("\" style=\"max-width: 100%; margin: 10px 0;\" />");
            }
            content.append("</div>");
        }

        // 添加位置信息
        if (moment.getLocation() != null && !moment.getLocation().isEmpty()) {
            content.append("<p><em>📍 ").append(moment.getLocation()).append("</em></p>");
        }

        return content.toString();
    }

    /**
     * RSS Item与时间的包装类，用于排序
     */
    private static class RssItemWithTime {
        private final RssItem item;
        private final Date time;

        public RssItemWithTime(RssItem item, java.util.Date time) {
            this.item = item;
            this.time = time;
        }

        public RssItem getItem() {
            return item;
        }

        public java.util.Date getTime() {
            return time;
        }
    }
}
