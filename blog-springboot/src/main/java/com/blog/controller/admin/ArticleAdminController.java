package com.blog.controller.admin;

import com.blog.common.result.Result;
import com.blog.dto.ArticleDTO;
import com.blog.service.ArticleService;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;
import com.blog.vo.PageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台文章管理 Controller
 */
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class ArticleAdminController {

    private final ArticleService articleService;

    /**
     * 分页查询文章列表（支持分类/标签/状态/关键词筛选）
     */
    @GetMapping
    public Result<PageVO<ArticleListVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(articleService.adminList(pageNum, pageSize, categoryId, tagId, status, keyword));
    }

    /**
     * 根据 ID 查询文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleVO> getById(@PathVariable Long id) {
        return Result.success(articleService.getById(id));
    }

    /**
     * 创建文章
     */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ArticleDTO dto) {
        articleService.create(dto);
        return Result.success();
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ArticleDTO dto) {
        articleService.update(id, dto);
        return Result.success();
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success();
    }
}
