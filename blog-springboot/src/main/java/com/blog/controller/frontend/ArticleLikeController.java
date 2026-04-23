package com.blog.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.blog.common.result.Result;
import com.blog.entity.Article;
import com.blog.entity.ArticleLike;
import com.blog.mapper.ArticleLikeMapper;
import com.blog.mapper.ArticleMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 文章点赞接口
 */
@Slf4j
@RestController
@RequestMapping("/api/articles")
@Validated
@RequiredArgsConstructor
public class ArticleLikeController {

    private final ArticleLikeMapper likeMapper;
    private final ArticleMapper articleMapper;

    /**
     * 点赞 / 取消点赞（同一 IP 重复点击则取消）
     */
    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> like(@PathVariable Long id, HttpServletRequest request) {
        String ip = getClientIp(request);
        Map<String, Object> result = new HashMap<>();

        // 检查是否已点赞
        ArticleLike existing = likeMapper.selectOne(
                new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getArticleId, id)
                        .eq(ArticleLike::getLikeIp, ip)
        );

        if (existing != null) {
            // 取消点赞
            likeMapper.deleteById(existing.getId());
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                    .eq(Article::getId, id)
                    .setSql("like_count = GREATEST(like_count - 1, 0)"));
            result.put("liked", false);
        } else {
            // 点赞
            ArticleLike like = new ArticleLike();
            like.setArticleId(id);
            like.setLikeIp(ip);
            like.setCreateTime(LocalDateTime.now());
            likeMapper.insert(like);
            articleMapper.update(null, new LambdaUpdateWrapper<Article>()
                    .eq(Article::getId, id)
                    .setSql("like_count = like_count + 1"));
            result.put("liked", true);
        }

        // 返回最新点赞数
        Article article = articleMapper.selectById(id);
        result.put("likeCount", article != null ? article.getLikeCount() : 0);
        return Result.success(result);
    }

    /** 查询当前 IP 是否已点赞 */
    @GetMapping("/{id}/like")
    public Result<Map<String, Object>> likeStatus(@PathVariable Long id, HttpServletRequest request) {
        String ip = getClientIp(request);
        boolean liked = likeMapper.selectCount(
                new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getArticleId, id)
                        .eq(ArticleLike::getLikeIp, ip)
        ) > 0;
        Article article = articleMapper.selectById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", article != null ? article.getLikeCount() : 0);
        return Result.success(result);
    }

    private String getClientIp(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP"};
        for (String h : headers) {
            String ip = request.getHeader(h);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
