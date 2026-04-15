package com.blog.property;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.service.impl.ArticleServiceImpl;
import com.blog.service.impl.CategoryServiceImpl;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

/**
 * 文章和分类相关属性测试
 * Feature: personal-blog-system
 */
class ArticlePropertyTest {

    // Feature: personal-blog-system, Property 1: 软删除后访客不可见
    @Property(tries = 100)
    void 软删除后访客查询不到该文章(@ForAll("positiveIds") Long articleId) {
        // 准备 mock
        ArticleMapper articleMapper = Mockito.mock(ArticleMapper.class);
        ArticleTagMapper articleTagMapper = Mockito.mock(ArticleTagMapper.class);
        ArticleServiceImpl articleService = new ArticleServiceImpl(articleMapper, articleTagMapper);

        // mock：前台查询文章详情返回 null（模拟软删除后不可见）
        Mockito.when(articleMapper.selectArticleVOById(articleId)).thenReturn(null);

        // 断言：frontGetById 应抛出 BusinessException（文章不存在）
        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> articleService.frontGetById(articleId));
        Assertions.assertEquals(ResultCode.ARTICLE_NOT_FOUND, ex.getResultCode());
    }

    // Feature: personal-blog-system, Property 2: 含文章的分类不可删除
    @Property(tries = 100)
    void 含文章的分类不可删除(@ForAll("positiveIds") Long categoryId) {
        // 准备 mock
        CategoryMapper categoryMapper = Mockito.mock(CategoryMapper.class);
        ArticleMapper articleMapper = Mockito.mock(ArticleMapper.class);
        CategoryServiceImpl categoryService = new CategoryServiceImpl(categoryMapper, articleMapper);

        // mock：该分类下有文章（返回 1L 表示有文章）
        Mockito.when(articleMapper.selectCount(Mockito.any(LambdaQueryWrapper.class))).thenReturn(1L);

        // 断言：delete 应抛出 BusinessException（分类下有文章）
        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> categoryService.delete(categoryId));
        Assertions.assertEquals(ResultCode.CATEGORY_HAS_ARTICLES, ex.getResultCode());
    }

    @Provide
    Arbitrary<Long> positiveIds() {
        return Arbitraries.longs().between(1L, 100000L);
    }
}
