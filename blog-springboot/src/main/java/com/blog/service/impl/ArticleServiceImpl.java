package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.dto.ArticleDTO;
import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.ArticleTagMapper;
import com.blog.service.ArticleService;
import com.blog.vo.ArchiveVO;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;
import com.blog.vo.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleTagMapper articleTagMapper;

    /**
     * 后台分页查询文章列表
     */
    @Override
    public PageVO<ArticleListVO> adminList(Integer pageNum, Integer pageSize,
                                           Long categoryId, Long tagId,
                                           Integer status, String keyword) {
        Page<ArticleListVO> page = new Page<>(pageNum, pageSize);
        articleMapper.selectArticleListVO(page, categoryId, tagId, status, keyword);
        return PageVO.of(page);
    }

    /**
     * 后台根据 ID 查询文章详情
     */
    @Override
    public ArticleVO getById(Long id) {
        ArticleVO vo = articleMapper.selectArticleVOById(id);
        if (vo == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        return vo;
    }

    /**
     * 创建文章，并批量插入标签关联
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(ArticleDTO dto) {
        // 构建文章实体
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setSummary(dto.getSummary());
        article.setCoverUrl(dto.getCoverUrl());
        article.setCategoryId(dto.getCategoryId());
        article.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        article.setViewCount(0);
        articleMapper.insert(article);

        // 批量插入文章标签关联
        if (!CollectionUtils.isEmpty(dto.getTagIds())) {
            insertArticleTags(article.getId(), dto.getTagIds());
        }
    }

    /**
     * 更新文章，先删除旧标签关联再插入新的
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ArticleDTO dto) {
        // 更新文章基本信息
        Article article = new Article();
        article.setId(id);
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setSummary(dto.getSummary());
        article.setCoverUrl(dto.getCoverUrl());
        article.setCategoryId(dto.getCategoryId());
        article.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        articleMapper.updateById(article);

        // 删除旧的标签关联
        articleTagMapper.delete(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, id)
        );

        // 插入新的标签关联
        if (!CollectionUtils.isEmpty(dto.getTagIds())) {
            insertArticleTags(id, dto.getTagIds());
        }
    }

    /**
     * 软删除文章（MyBatis-Plus @TableLogic 自动处理）
     */
    @Override
    public void delete(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public void toggleTop(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            Article update = new Article();
            update.setId(id);
            update.setIsTop(article.getIsTop() != null && article.getIsTop() == 1 ? 0 : 1);
            articleMapper.updateById(update);
        }
    }

    /**
     * 前台分页查询已发布文章列表（固定 status=1）
     */
    @Override
    public PageVO<ArticleListVO> frontList(Integer pageNum, Integer pageSize,
                                           Long categoryId, Long tagId, String keyword) {
        Page<ArticleListVO> page = new Page<>(pageNum, pageSize);
        articleMapper.selectArticleListVO(page, categoryId, tagId, 1, keyword);
        return PageVO.of(page);
    }

    /**
     * 前台根据 ID 查询已发布文章详情，并增加阅读次数
     */
    @Override
    public ArticleVO frontGetById(Long id) {
        ArticleVO vo = articleMapper.selectArticleVOById(id);
        if (vo == null || vo.getStatus() != 1) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }
        // 阅读次数 +1
        articleMapper.update(null,
                new LambdaUpdateWrapper<Article>()
                        .eq(Article::getId, id)
                        .setSql("view_count = view_count + 1")
        );
        vo.setViewCount(vo.getViewCount() + 1);
        return vo;
    }

    /**
     * 查询文章归档数据，按年月分组并填充文章列表
     */
    @Override
    public List<ArchiveVO> archive() {
        // 查询归档统计（年月 + 数量）
        List<ArchiveVO> archiveList = articleMapper.selectArchive();
        if (CollectionUtils.isEmpty(archiveList)) {
            return archiveList;
        }

        // 查询所有已发布文章列表（不分页，取全量）
        Page<ArticleListVO> page = new Page<>(1, Integer.MAX_VALUE);
        articleMapper.selectArticleListVO(page, null, null, 1, null);
        List<ArticleListVO> allArticles = page.getRecords();

        // 按年月分组文章
        Map<String, List<ArticleListVO>> articlesByMonth = allArticles.stream()
                .collect(Collectors.groupingBy(a -> {
                    // 从 createTime 提取 yyyy-MM 格式
                    return a.getCreateTime().getYear() + "-"
                            + String.format("%02d", a.getCreateTime().getMonthValue());
                }));

        // 填充每个归档月份的文章列表
        for (ArchiveVO archive : archiveList) {
            List<ArticleListVO> articles = articlesByMonth.get(archive.getYearMonth());
            archive.setArticles(articles != null ? articles : new ArrayList<>());
        }

        return archiveList;
    }

    /**
     * 批量插入文章标签关联记录
     */
    private void insertArticleTags(Long articleId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(tagId);
            articleTagMapper.insert(articleTag);
        }
    }
}
