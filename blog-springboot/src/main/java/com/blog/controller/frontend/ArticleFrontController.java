package com.blog.controller.frontend;

import com.blog.common.result.Result;
import com.blog.service.ArticleService;
import com.blog.vo.ArchiveVO;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;
import com.blog.vo.PageVO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台文章 Controller
 * 注意：/archive 路径定义在 /{id} 之前，避免路由冲突
 */
@RestController
@RequestMapping("/api/articles")
@Validated
@RequiredArgsConstructor
public class ArticleFrontController {

    private final ArticleService articleService;

    /**
     * 分页查询已发布文章列表
     */
    @GetMapping
    public Result<PageVO<ArticleListVO>> list(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "pageNum 最小为 1") Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "pageSize 最小为 1")
            @Max(value = 100, message = "pageSize 最大为 100") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword) {
        return Result.success(articleService.frontList(pageNum, pageSize, categoryId, tagId, keyword));
    }

    /**
     * 查询文章归档数据（按年月分组）
     * 此路径必须在 /{id} 之前定义
     */
    @GetMapping("/archive")
    public Result<List<ArchiveVO>> archive() {
        return Result.success(articleService.archive());
    }

    /**
     * 根据 ID 查询已发布文章详情，并增加阅读次数
     */
    @GetMapping("/{id}")
    public Result<ArticleVO> getById(@PathVariable Long id) {
        return Result.success(articleService.frontGetById(id));
    }

    /**
     * 获取相关文章推荐
     */
    @GetMapping("/{id}/related")
    public Result<List<ArticleListVO>> related(@PathVariable Long id,
                                               @RequestParam(defaultValue = "5")
                                               @Min(value = 1, message = "limit 最小为 1")
                                               @Max(value = 20, message = "limit 最大为 20") Integer limit) {
        return Result.success(articleService.getRelated(id, limit));
    }
}
