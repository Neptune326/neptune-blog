package com.blog.service;

import com.blog.dto.ArticleDTO;
import com.blog.vo.ArchiveVO;
import com.blog.vo.ArticleListVO;
import com.blog.vo.ArticleVO;
import com.blog.vo.PageVO;

import java.util.List;

/**
 * 文章服务接口（后台 + 前台）
 */
public interface ArticleService {

    // ===== 后台接口 =====

    /**
     * 后台分页查询文章列表，支持多条件筛选
     */
    PageVO<ArticleListVO> adminList(Integer pageNum, Integer pageSize,
                                    Long categoryId, Long tagId,
                                    Integer status, String keyword);

    /**
     * 后台根据 ID 查询文章详情
     */
    ArticleVO getById(Long id);

    /**
     * 创建文章
     */
    void create(ArticleDTO dto);

    /**
     * 更新文章
     */
    void update(Long id, ArticleDTO dto);

    /**
     * 删除文章（软删除）
     */
    void delete(Long id);

    /** 切换置顶状态 */
    void toggleTop(Long id);

    /** 批量修改文章状态 */
    void batchUpdateStatus(java.util.List<Long> ids, Integer status);
    
    /** 批量删除文章（软删除） */
    void batchDelete(java.util.List<Long> ids);

    // ===== 前台接口 =====

    /**
     * 前台分页查询已发布文章列表
     */
    PageVO<ArticleListVO> frontList(Integer pageNum, Integer pageSize,
                                    Long categoryId, Long tagId, String keyword);

    /**
     * 前台根据 ID 查询已发布文章详情，并增加阅读次数
     */
    ArticleVO frontGetById(Long id);

    /**
     * 查询文章归档数据（按年月分组）
     */
    List<ArchiveVO> archive();

    /** 获取相关文章（根据标签/分类推荐，排除自身） */
    List<ArticleListVO> getRelated(Long articleId, Integer limit);
}
