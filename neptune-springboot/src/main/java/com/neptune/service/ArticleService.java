package com.neptune.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.neptune.dto.ArticleDto;
import com.neptune.entity.Article;
import com.neptune.vo.admin.ArticleVo;
import com.neptune.vo.blog.BlogArticleViewVo;

import java.util.Map;

/**
 * 文章表 服务层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public interface ArticleService extends IService<Article> {

    boolean saveUpdate(ArticleDto dto);

    Page<ArticleVo> getPage(Integer pageNumber, Integer pageSize, Map<String, String> search);

    BlogArticleViewVo getBlogViewInfo(Long id);
}
