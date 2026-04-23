package com.blog.controller.frontend;

import com.blog.common.result.Result;
import com.blog.service.ArticleService;
import com.blog.service.TagService;
import com.blog.vo.ArticleListVO;
import com.blog.vo.PageVO;
import com.blog.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台标签 Controller
 */
@RestController
@RequestMapping("/api/tags")
@Validated
@RequiredArgsConstructor
public class TagFrontController {

    private final TagService tagService;
    private final ArticleService articleService;

    /**
     * 查询所有标签列表
     */
    @GetMapping
    public Result<List<TagVO>> list() {
        return Result.success(tagService.list());
    }

    /**
     * 查询指定标签下的已发布文章列表
     */
    @GetMapping("/{id}/articles")
    public Result<PageVO<ArticleListVO>> articlesByTag(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(articleService.frontList(pageNum, pageSize, null, id, null));
    }
}
