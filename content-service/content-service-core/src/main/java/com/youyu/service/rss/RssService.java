package com.youyu.service.rss;

import com.youyu.dto.rss.RssChannel;
import com.youyu.dto.rss.RssItem;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * RSS服务类
 *
 * @author youyu
 */
@Service
public class RssService {

    /**
     * 生成RSS 2.0格式的XML字符串
     *
     * @param channel RSS频道信息
     * @return RSS XML字符串
     */
    public String generateRssXml(RssChannel channel) {
        StringBuilder xml = new StringBuilder();

        // XML声明和RSS根元素
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<rss version=\"2.0\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        xml.append("  <channel>\n");

        // 频道信息
        xml.append("    <title>").append(escapeXml(channel.getTitle())).append("</title>\n");
        xml.append("    <link>").append(escapeXml(channel.getLink())).append("</link>\n");
        xml.append("    <description>").append(escapeXml(channel.getDescription())).append("</description>\n");
        xml.append("    <language>").append(channel.getLanguage()).append("</language>\n");
        xml.append("    <lastBuildDate>").append(formatRFC822Date(new Date())).append("</lastBuildDate>\n");
        xml.append("    <generator>").append(escapeXml(channel.getGenerator())).append("</generator>\n");

        // atom:link自引用
        xml.append("    <atom:link href=\"").append(escapeXml(channel.getLink())).append("\" rel=\"self\" type=\"application/rss+xml\" />\n");

        // 遍历所有项目
        for (RssItem item : channel.getItems()) {
            xml.append("    <item>\n");

            // 标题
            if (item.getTitle() != null) {
                xml.append("      <title>").append(escapeXml(item.getTitle())).append("</title>\n");
            }

            // 链接
            if (item.getLink() != null) {
                xml.append("      <link>").append(escapeXml(item.getLink())).append("</link>\n");
            }

            // GUID
            if (item.getGuid() != null) {
                xml.append("      <guid isPermaLink=\"false\">").append(escapeXml(item.getGuid())).append("</guid>\n");
            }

            // 描述/摘要
            if (item.getDescription() != null) {
                xml.append("      <description>").append(escapeCData(item.getDescription())).append("</description>\n");
            }

            // 作者
            if (item.getAuthor() != null) {
                xml.append("      <author>").append(escapeXml(item.getAuthor())).append("</author>\n");
            }

            // 分类
            if (item.getCategory() != null) {
                xml.append("      <category>").append(escapeXml(item.getCategory())).append("</category>\n");
            }

            // 发布日期
            if (item.getPubDate() != null) {
                xml.append("      <pubDate>").append(item.getPubDate()).append("</pubDate>\n");
            }

            // 全文内容
            if (item.getContent() != null) {
                xml.append("      <content:encoded>").append(escapeCData(item.getContent())).append("</content:encoded>\n");
            }

            // 封面图片
            if (item.getEnclosure() != null) {
                xml.append("      <enclosure url=\"").append(escapeXml(item.getEnclosure()))
                        .append("\" type=\"").append(item.getEnclosureType()).append("\" />\n");
            }

            xml.append("    </item>\n");
        }

        xml.append("  </channel>\n");
        xml.append("</rss>");

        return xml.toString();
    }

    /**
     * XML特殊字符转义
     *
     * @param text 原始文本
     * @return 转义后的文本
     */
    private String escapeXml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    /**
     * 使用CDATA包装内容（用于HTML内容）
     *
     * @param text 原始文本
     * @return CDATA包装的文本
     */
    private String escapeCData(String text) {
        if (text == null) {
            return "<![CDATA[]]>";
        }
        // 移除CDATA中的结束标记，防止CDATA嵌套
        String safeCData = text.replace("]]>", "]]]]><![CDATA[>");
        return "<![CDATA[" + safeCData + "]]>";
    }

    /**
     * 格式化日期为RFC 822格式
     *
     * @param date 日期对象
     * @return RFC 822格式的日期字符串
     */
    public String formatRFC822Date(Date date) {
        SimpleDateFormat rfc822DateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        rfc822DateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return rfc822DateFormat.format(date);
    }

    /**
     * 截取摘要（如果文本过长）
     *
     * @param content   原始内容
     * @param maxLength 最大长度
     * @return 截取后的摘要
     */
    public String truncateSummary(String content, int maxLength) {
        if (content == null) {
            return "";
        }
        // 移除HTML标签
        String plainText = content.replaceAll("<[^>]+>", "");
        if (plainText.length() <= maxLength) {
            return plainText;
        }
        return plainText.substring(0, maxLength) + "...";
    }
}
