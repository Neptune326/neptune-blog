package com.blog.controller.frontend;

import com.blog.common.result.Result;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.vo.ArticleListVO;
import com.blog.vo.CategoryVO;
import com.blog.vo.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台分类 Controller
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryFrontController {

    private final CategoryService categoryService;
    private final ArticleService articleService;

    /**
     * 查询所有分类列表
     */
    @GetMapping
    public Result<List<CategoryVO>> list() {
        return Result.success(categoryService.list());
    }

    /**
     * 查询指定分类下的已发布文章列表
     */
    @GetMapping("/{id}/articles")
    public Result<PageVO<ArticleListVO>> articlesByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(articleService.frontList(pageNum, pageSize, id, null, null));
    }
}
