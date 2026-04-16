package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.annotation.OperationLog;
import com.blog.common.result.Result;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import com.blog.service.ArticleService;
import com.blog.dto.ArticleDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 文章导入导出接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class ArticleImportExportController {

    private final ArticleMapper articleMapper;
    private final ArticleService articleService;

    /**
     * 导出所有已发布文章为 Markdown ZIP 包
     * GET /api/admin/articles/export
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1)
        );

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + URLEncoder.encode("articles.zip", "UTF-8"));

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream(), StandardCharsets.UTF_8)) {
            for (Article article : articles) {
                // 文件名：ID-标题.md
                String fileName = article.getId() + "-" + sanitizeFileName(article.getTitle()) + ".md";
                zos.putNextEntry(new ZipEntry(fileName));

                // 写入 Front Matter + 正文
                StringBuilder sb = new StringBuilder();
                sb.append("---\n");
                sb.append("title: ").append(article.getTitle()).append("\n");
                if (article.getSummary() != null) sb.append("summary: ").append(article.getSummary()).append("\n");
                if (article.getCreateTime() != null) sb.append("date: ").append(article.getCreateTime()).append("\n");
                sb.append("---\n\n");
                sb.append(article.getContent() != null ? article.getContent() : "");

                zos.write(sb.toString().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
        }
        log.info("导出文章 {} 篇", articles.size());
    }

    /**
     * 从 ZIP 包批量导入 Markdown 文章
     * POST /api/admin/articles/import
     */
    @OperationLog(module = "文章管理", action = "批量导入文章")
    @PostMapping("/import")
    public Result<String> importArticles(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error(com.blog.common.result.ResultCode.BAD_REQUEST, "文件不能为空");
        }

        List<String> imported = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream(), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if (!name.endsWith(".md")) continue;

                // 读取文件内容
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int len;
                while ((len = zis.read(buf)) > 0) baos.write(buf, 0, len);
                String content = baos.toString(StandardCharsets.UTF_8);

                try {
                    // 解析 Front Matter
                    ArticleDTO dto = parseFrontMatter(content, name);
                    articleService.create(dto);
                    imported.add(dto.getTitle());
                } catch (Exception e) {
                    failed.add(name + ": " + e.getMessage());
                    log.warn("导入文章失败: {}", name, e);
                }
                zis.closeEntry();
            }
        }

        String msg = "成功导入 " + imported.size() + " 篇";
        if (!failed.isEmpty()) msg += "，失败 " + failed.size() + " 篇";
        log.info("批量导入完成：{}", msg);
        return Result.success(msg);
    }

    /** 解析 Markdown Front Matter */
    private ArticleDTO parseFrontMatter(String content, String fileName) {
        ArticleDTO dto = new ArticleDTO();
        dto.setStatus(0); // 默认草稿

        if (content.startsWith("---")) {
            int end = content.indexOf("---", 3);
            if (end > 0) {
                String frontMatter = content.substring(3, end).trim();
                String body = content.substring(end + 3).trim();
                dto.setContent(body);

                for (String line : frontMatter.split("\n")) {
                    if (line.startsWith("title:")) {
                        dto.setTitle(line.substring(6).trim());
                    } else if (line.startsWith("summary:")) {
                        dto.setSummary(line.substring(8).trim());
                    }
                }
            } else {
                dto.setContent(content);
            }
        } else {
            dto.setContent(content);
        }

        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            // 从文件名提取标题
            dto.setTitle(fileName.replaceAll("^\\d+-", "").replace(".md", ""));
        }
        return dto;
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").substring(0, Math.min(name.length(), 50));
    }
}
