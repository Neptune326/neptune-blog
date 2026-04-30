package com.blog.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.entity.ArticleFavorite;
import com.blog.mapper.ArticleFavoriteMapper;
import com.blog.mapper.ArticleMapper;
import com.blog.vo.ArticleListVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/articles")
@Validated
@RequiredArgsConstructor
public class ArticleFavoriteController {
    private final ArticleFavoriteMapper favoriteMapper;
    private final ArticleMapper articleMapper;

    @PostMapping("/{id}/favorite")
    public Result<Map<String, Object>> favorite(@PathVariable Long id, HttpServletRequest request) {
        String visitorId = resolveVisitorId(request);
        ArticleFavorite existing = favoriteMapper.selectOne(
                new LambdaQueryWrapper<ArticleFavorite>()
                        .eq(ArticleFavorite::getArticleId, id)
                        .eq(ArticleFavorite::getVisitorId, visitorId)
        );
        boolean favorited;
        if (existing == null) {
            ArticleFavorite favorite = new ArticleFavorite();
            favorite.setArticleId(id);
            favorite.setVisitorId(visitorId);
            favorite.setCreateTime(LocalDateTime.now());
            favoriteMapper.insert(favorite);
            favorited = true;
        } else {
            favoriteMapper.deleteById(existing.getId());
            favorited = false;
        }
        long count = favoriteMapper.selectCount(new LambdaQueryWrapper<ArticleFavorite>()
                .eq(ArticleFavorite::getArticleId, id));
        Map<String, Object> result = new HashMap<>();
        result.put("favorited", favorited);
        result.put("favoriteCount", count);
        return Result.success(result);
    }

    @GetMapping("/{id}/favorite")
    public Result<Map<String, Object>> favoriteStatus(@PathVariable Long id, HttpServletRequest request) {
        String visitorId = resolveVisitorId(request);
        long liked = favoriteMapper.selectCount(new LambdaQueryWrapper<ArticleFavorite>()
                .eq(ArticleFavorite::getArticleId, id)
                .eq(ArticleFavorite::getVisitorId, visitorId));
        long count = favoriteMapper.selectCount(new LambdaQueryWrapper<ArticleFavorite>()
                .eq(ArticleFavorite::getArticleId, id));
        Map<String, Object> result = new HashMap<>();
        result.put("favorited", liked > 0);
        result.put("favoriteCount", count);
        return Result.success(result);
    }

    @GetMapping("/favorites")
    public Result<List<ArticleListVO>> myFavorites(HttpServletRequest request) {
        String visitorId = resolveVisitorId(request);
        List<ArticleFavorite> favorites = favoriteMapper.selectList(new LambdaQueryWrapper<ArticleFavorite>()
                .eq(ArticleFavorite::getVisitorId, visitorId)
                .orderByDesc(ArticleFavorite::getCreateTime)
                .last("LIMIT 100"));
        List<ArticleListVO> result = new ArrayList<>();
        for (ArticleFavorite favorite : favorites) {
            com.blog.vo.ArticleVO article = articleMapper.selectArticleVOById(favorite.getArticleId());
            if (article != null && article.getStatus() != null && article.getStatus() == 1) {
                ArticleListVO vo = new ArticleListVO();
                org.springframework.beans.BeanUtils.copyProperties(article, vo);
                result.add(vo);
            }
        }
        return Result.success(result);
    }

    private String resolveVisitorId(HttpServletRequest request) {
        String header = request.getHeader("X-Visitor-Id");
        if (header != null && !header.isBlank()) {
            return header.trim();
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0].trim();
        }
        String ua = Optional.ofNullable(request.getHeader("User-Agent")).orElse("unknown");
        return sha256(ip + "|" + ua);
    }

    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }
}
