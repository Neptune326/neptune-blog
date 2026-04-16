package com.blog.controller.admin;

import com.blog.common.annotation.OperationLog;
import com.blog.common.result.Result;
import com.blog.dto.ArticleDTO;
import com.blog.service.ArticleService;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;
import com.blog.vo.PageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 后台文章管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class ArticleAdminController {

    private final ArticleService articleService;

    @GetMapping
    public Result<PageVO<ArticleListVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        log.debug("查询文章列表，pageNum={}, pageSize={}", pageNum, pageSize);
        return Result.success(articleService.adminList(pageNum, pageSize, categoryId, tagId, status, keyword));
    }

    @GetMapping("/{id}")
    public Result<ArticleVO> getById(@PathVariable Long id) {
        return Result.success(articleService.getById(id));
    }

    @OperationLog(module = "文章管理", action = "创建文章")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ArticleDTO dto) {
        log.info("创建文章：{}", dto.getTitle());
        articleService.create(dto);
        return Result.success();
    }

    @OperationLog(module = "文章管理", action = "更新文章")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ArticleDTO dto) {
        log.info("更新文章 ID={}：{}", id, dto.getTitle());
        articleService.update(id, dto);
        return Result.success();
    }

    @OperationLog(module = "文章管理", action = "删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除文章 ID={}", id);
        articleService.delete(id);
        return Result.success();
    }

    /** 切换置顶状态 */
    @OperationLog(module = "文章管理", action = "切换置顶")
    @PutMapping("/{id}/top")
    public Result<Void> toggleTop(@PathVariable Long id) {
        log.info("切换文章置顶 ID={}", id);
        articleService.toggleTop(id);
        return Result.success();
    }
}
