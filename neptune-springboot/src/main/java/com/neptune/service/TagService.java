package com.neptune.service;

import com.mybatisflex.core.service.IService;
import com.neptune.entity.Tag;

/**
 * 菜单表 服务层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public interface TagService extends IService<Tag> {

    void updateTagCache();

    void setArticleCache(Long articleId);

    void delArticleCache(Long articleId);

    void loadArticleCache();

    String[] getTagNames(String articleId);

}
