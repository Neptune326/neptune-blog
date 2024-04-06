package com.neptune.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.ArticleTag;
import com.neptune.mapper.ArticleTagMapper;
import com.neptune.service.ArticleTagService;
import com.neptune.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.neptune.entity.table.ArticleTagTableDef.ARTICLE_TAG;
import static com.neptune.entity.table.TagTableDef.TAG;

/**
 * 角色资源关联表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {


}
