package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.annotation.OperationLog;
import com.blog.common.result.Result;
import com.blog.entity.Article;
import com.blog.entity.ArticleSeries;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleSeriesMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章系列管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/series")
@RequiredArgsConstructor
public class SeriesAdminController {

    private final ArticleSeriesMapper seriesMapper;
    private final ArticleMapper articleMapper;

    @GetMapping
    public Result<List<ArticleSeries>> list() {
        return Result.success(seriesMapper.selectList(
                new LambdaQueryWrapper<ArticleSeries>().orderByAsc(ArticleSeries::getSort)
        ));
    }

    @OperationLog(module = "系列管理", action = "创建系列")
    @PostMapping
    public Result<Void> create(@RequestBody ArticleSeries series) {
        log.info("创建系列：{}", series.getName());
        seriesMapper.insert(series);
        return Result.success();
    }

    @OperationLog(module = "系列管理", action = "更新系列")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ArticleSeries series) {
        series.setId(id);
        seriesMapper.updateById(series);
        return Result.success();
    }

    @OperationLog(module = "系列管理", action = "删除系列")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        // 解除文章与系列的关联
        Article update = new Article();
        update.setSeriesId(null);
        articleMapper.update(update, new LambdaQueryWrapper<Article>().eq(Article::getSeriesId, id));
        seriesMapper.deleteById(id);
        return Result.success();
    }

    /** 获取系列下的文章列表 */
    @GetMapping("/{id}/articles")
    public Result<List<Article>> articles(@PathVariable Long id) {
        return Result.success(articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getSeriesId, id)
                        .orderByAsc(Article::getSeriesSort)
        ));
    }
}
