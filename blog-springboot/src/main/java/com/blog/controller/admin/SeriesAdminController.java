package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.annotation.OperationLog;
import com.blog.common.result.Result;
import com.blog.dto.ArticleSeriesDTO;
import com.blog.entity.Article;
import com.blog.entity.ArticleSeries;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleSeriesMapper;
import com.blog.vo.ArticleSeriesVO;
import com.blog.vo.SeriesArticleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章系列管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/series")
@Validated
@RequiredArgsConstructor
public class SeriesAdminController {

    private final ArticleSeriesMapper seriesMapper;
    private final ArticleMapper articleMapper;

    @GetMapping
    public Result<List<ArticleSeriesVO>> list() {
        List<ArticleSeries> seriesList = seriesMapper.selectList(
                new LambdaQueryWrapper<ArticleSeries>().orderByAsc(ArticleSeries::getSort)
        );
        return Result.success(seriesList.stream().map(this::toArticleSeriesVO).collect(Collectors.toList()));
    }

    @OperationLog(module = "系列管理", action = "创建系列")
    @PostMapping
    public Result<Void> create(@RequestBody @Valid ArticleSeriesDTO seriesDTO) {
        ArticleSeries articleSeries = new ArticleSeries();
        BeanUtils.copyProperties(seriesDTO, articleSeries);
        log.info("创建系列：{}", articleSeries.getName());
        seriesMapper.insert(articleSeries);
        return Result.success();
    }

    @OperationLog(module = "系列管理", action = "更新系列")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid ArticleSeriesDTO seriesDTO) {
        ArticleSeries articleSeries = new ArticleSeries();
        BeanUtils.copyProperties(seriesDTO, articleSeries);
        articleSeries.setId(id);
        seriesMapper.updateById(articleSeries);
        return Result.success();
    }

    @OperationLog(module = "系列管理", action = "删除系列")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Article update = new Article();
        update.setSeriesId(null);
        articleMapper.update(update, new LambdaQueryWrapper<Article>().eq(Article::getSeriesId, id));
        seriesMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}/articles")
    public Result<List<SeriesArticleVO>> articles(@PathVariable Long id) {
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getSeriesId, id)
                        .orderByAsc(Article::getSeriesSort)
        );
        return Result.success(articles.stream().map(this::toSeriesArticleVO).collect(Collectors.toList()));
    }

    private ArticleSeriesVO toArticleSeriesVO(ArticleSeries articleSeries) {
        ArticleSeriesVO articleSeriesVO = new ArticleSeriesVO();
        BeanUtils.copyProperties(articleSeries, articleSeriesVO);
        return articleSeriesVO;
    }

    private SeriesArticleVO toSeriesArticleVO(Article article) {
        SeriesArticleVO seriesArticleVO = new SeriesArticleVO();
        BeanUtils.copyProperties(article, seriesArticleVO);
        return seriesArticleVO;
    }
}