package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.entity.Article;
import com.blog.vo.ArchiveVO;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章 Mapper 接口
 * 继承 BaseMapper 获得基础 CRUD 操作，并提供复杂联表查询的自定义方法
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 分页查询文章列表视图（含分类名和标签列表）
     * 支持按分类、标签、发布状态、关键词动态筛选，按创建时间倒序排列
     *
     * @param page       分页参数
     * @param categoryId 分类 ID 筛选条件（可为 null）
     * @param tagId      标签 ID 筛选条件（可为 null）
     * @param status     发布状态筛选条件（可为 null）
     * @param keyword    关键词搜索（匹配标题或摘要，可为 null）
     * @return 分页文章列表视图对象
     */
    IPage<ArticleListVO> selectArticleListVO(
            IPage<?> page,
            @Param("categoryId") Long categoryId,
            @Param("tagId") Long tagId,
            @Param("status") Integer status,
            @Param("keyword") String keyword
    );

    /**
     * 根据 ID 查询文章详情视图（含分类名和标签列表）
     *
     * @param id 文章 ID
     * @return 文章详情视图对象，不存在时返回 null
     */
    ArticleVO selectArticleVOById(@Param("id") Long id);

    /**
     * 查询已发布文章的归档数据
     * 按年月分组统计文章数量，按年月倒序排列
     *
     * @return 归档视图对象列表（仅包含年月和数量，不含文章列表）
     */
    List<ArchiveVO> selectArchive();
}
