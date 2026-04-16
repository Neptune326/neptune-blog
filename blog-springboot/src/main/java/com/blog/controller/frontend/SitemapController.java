package com.blog.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import com.blog.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Sitemap 生成接口
 * 访问 /sitemap.xml 获取站点地图，提交给搜索引擎
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class SitemapController {

    private final ArticleMapper articleMapper;
    private final SysConfigService sysConfigService;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String sitemap() {
        if (!sysConfigService.getBoolValue("sitemap_enabled")) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"></urlset>";
        }

        String blogName = sysConfigService.getValue("blog_name");
        // 实际部署时替换为真实域名
        String baseUrl = "https://yourdomain.com";

        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .orderByDesc(Article::getUpdateTime)
        );

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // 首页
        xml.append("  <url><loc>").append(baseUrl).append("/</loc><changefreq>daily</changefreq><priority>1.0</priority></url>\n");
        xml.append("  <url><loc>").append(baseUrl).append("/archive</loc><changefreq>weekly</changefreq><priority>0.8</priority></url>\n");
        xml.append("  <url><loc>").append(baseUrl).append("/about</loc><changefreq>monthly</changefreq><priority>0.7</priority></url>\n");

        // 文章页
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Article article : articles) {
            xml.append("  <url>")
               .append("<loc>").append(baseUrl).append("/article/").append(article.getId()).append("</loc>")
               .append("<lastmod>").append(article.getUpdateTime() != null ? article.getUpdateTime().format(fmt) : "").append("</lastmod>")
               .append("<changefreq>monthly</changefreq>")
               .append("<priority>0.6</priority>")
               .append("</url>\n");
        }

        xml.append("</urlset>");
        log.debug("Sitemap 生成完成，共 {} 篇文章", articles.size());
        return xml.toString();
    }
}
