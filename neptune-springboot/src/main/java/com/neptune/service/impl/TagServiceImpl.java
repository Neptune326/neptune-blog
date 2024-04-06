package com.neptune.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.constant.CacheConstant;
import com.neptune.entity.Tag;
import com.neptune.mapper.TagMapper;
import com.neptune.service.TagService;
import com.neptune.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.neptune.entity.table.ArticleTagTableDef.ARTICLE_TAG;
import static com.neptune.entity.table.TagTableDef.TAG;

/**
 * 菜单表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public void updateTagCache() {
        CompletableFuture.runAsync(() -> {
            List<Tag> list = this.list();
            Map<String, Object> map = list.stream().collect(Collectors.toMap(
                    k -> Long.toString(k.getId()),
                    v -> v,
                    (k1, k2) -> k1
            ));
            redisUtils.del(CacheConstant.CACHE_KEY_ARTICLE_TAG);
            redisUtils.hmset(CacheConstant.CACHE_KEY_ARTICLE_TAG, map);
        });
    }


    @Override
    public void setArticleCache(Long articleId) {
        CompletableFuture.runAsync(() -> {

            List<Tag> list = this.list(
                    QueryWrapper.create()
                            .select(TAG.ALL_COLUMNS)
                            .from(ARTICLE_TAG)
                            .innerJoin(TAG).on(ARTICLE_TAG.TAG_ID.eq(TAG.ID))
                            .where(ARTICLE_TAG.ARTICLE_ID.eq(articleId)
                            )
            );
            if (CollUtil.isNotEmpty(list)) {
                redisUtils.hset(CacheConstant.CACHE_KEY_ARTICLE_TAG_HASH, Long.toString(articleId), list);
            }
        });
    }

    @Override
    public void delArticleCache(Long articleId) {
        CompletableFuture.runAsync(() -> {

            redisUtils.hdel(CacheConstant.CACHE_KEY_ARTICLE_TAG_HASH, Long.toString(articleId));
        });

    }

    @Override
    public void loadArticleCache() {
        CompletableFuture.runAsync(()->{
            redisUtils.del(CacheConstant.CACHE_KEY_ARTICLE_TAG_HASH);
            List<Map> list = this.listAs(
                    QueryWrapper.create()
                            .select(TAG.ALL_COLUMNS, ARTICLE_TAG.ARTICLE_ID.as("articleId"))
                            .from(ARTICLE_TAG)
                            .innerJoin(TAG).on(ARTICLE_TAG.TAG_ID.eq(TAG.ID)),
                    Map.class
            );
            if (CollUtil.isNotEmpty(list)) {
                list.stream().collect(Collectors.groupingBy(
                        k -> k.get("articleId").toString()
                )).forEach(
                        (k, v) -> {
                            redisUtils.hset(
                                    CacheConstant.CACHE_KEY_ARTICLE_TAG_HASH,
                                    k,
                                    v.stream().map(m -> BeanUtil.mapToBean(m, Tag.class, true)).collect(Collectors.toList())
                            );
                        }
                );
            }
        });
    }

    @Override
    public String[] getTagNames(String articleId) {
        List<Tag> list = (List<Tag>) redisUtils.hget(CacheConstant.CACHE_KEY_ARTICLE_TAG_HASH, articleId);
        if (CollUtil.isNotEmpty(list)) {
            return list.stream().map(Tag::getName).toArray(String[]::new);
        }
        return new String[0];
    }
}
