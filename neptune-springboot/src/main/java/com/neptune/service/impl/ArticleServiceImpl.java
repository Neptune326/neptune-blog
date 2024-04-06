package com.neptune.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.SqlUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.dto.ArticleDto;
import com.neptune.entity.Article;
import com.neptune.entity.ArticleTag;
import com.neptune.mapper.ArticleMapper;
import com.neptune.service.ArticleService;
import com.neptune.service.ArticleTagService;
import com.neptune.service.TagService;
import com.neptune.utils.RedisUtils;
import com.neptune.vo.admin.ArticleVo;
import com.neptune.vo.blog.BlogArticleAnchorVo;
import com.neptune.vo.blog.BlogArticleViewVo;
import org.apache.commons.compress.utils.Lists;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.neptune.constant.CacheConstant.CACHE_KEY_ARTICLE_ANCHOR;
import static com.neptune.entity.table.ArticleTableDef.ARTICLE;
import static com.neptune.entity.table.ArticleTagTableDef.ARTICLE_TAG;
import static com.neptune.entity.table.CategoryTableDef.CATEGORY;

/**
 * 文章表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private TagService tagService;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public boolean saveUpdate(ArticleDto dto) {
        Article t;
        if (Objects.nonNull(dto.getId())) {
            t = getById(dto.getId());
            BeanUtil.copyProperties(dto, t);
        } else {
            t = BeanUtil.copyProperties(dto, Article.class);
        }

        String content = t.getContent();
        Document doc = Jsoup.parse(content);
        Elements elements = doc.select("h1,h2,h3,h4,h5,h6");
        List<BlogArticleAnchorVo> list = Lists.newArrayList();
        if (!elements.isEmpty()) {
            for (int i = 0; i < elements.size(); i++) {
                Element ele = elements.get(i);
                String text = ele.text();
                if (StrUtil.isEmpty(text)) {
                    continue;
                }
                String tagName = ele.tagName();
                String level = tagName.substring(1);
                BlogArticleAnchorVo vo = BlogArticleAnchorVo.builder().index(i).text(text).level(level).build();
                list.add(vo);

                String eleHtml = ele.outerHtml();
                String newEleHtml = Jsoup.parse(eleHtml).selectFirst(tagName).addClass("neptune_anchor_" + i).outerHtml();
                content = content.replace(eleHtml, newEleHtml);
            }
            t.setContent(content);
        }

        boolean flag = saveOrUpdate(t);
        redisUtils.hset(CACHE_KEY_ARTICLE_ANCHOR, t.getId().toString(), list);

        articleTagService.remove(QueryWrapper.create().where(ARTICLE_TAG.ARTICLE_ID.eq(t.getId())));
        articleTagService.saveBatch(
                Arrays.stream(dto.getTagIds())
                        .map(tagId -> ArticleTag.builder().articleId(t.getId()).tagId(tagId).build())
                        .collect(Collectors.toList())
        );
        tagService.setArticleCache(t.getId());
        return flag;
    }

    @Override
    public Page<ArticleVo> getPage(Integer pageNumber, Integer pageSize, Map<String, String> search) {
        Page<ArticleVo> page = new Page<>(pageNumber, pageSize);
        this.pageAs(
                page,
                QueryWrapper.create()
                        .select(ARTICLE.ALL_COLUMNS, CATEGORY.NAME.as(ArticleVo::getCategoryName))
                        .from(ARTICLE)
                        .leftJoin(CATEGORY).on(ARTICLE.CATEGORY_ID.eq(CATEGORY.ID))
                        .where(ARTICLE.TITLE.like(search.get("title"), StrUtil.isNotBlank(search.get("title"))))
                        .and(ARTICLE.CATEGORY_ID.eq(search.get("categoryId"), StrUtil.isNotBlank(search.get("categoryId"))))
                        .orderBy(ARTICLE.CREATE_TIME.desc()),
                ArticleVo.class
        ).map(v -> {
            v.setTagNames(tagService.getTagNames(v.getId()));
            return v;
        });
        return page;
    }

    @Override
    public BlogArticleViewVo getBlogViewInfo(Long id) {
        Article t = this.getById(id);
        BlogArticleViewVo vo = BeanUtil.copyProperties(t, BlogArticleViewVo.class);
        String[] tagNames = tagService.getTagNames(vo.getId());
        vo.setTagNames(tagNames);
        vo.setAnchors((List<BlogArticleAnchorVo>) redisUtils.hget(CACHE_KEY_ARTICLE_ANCHOR, id.toString()));
        return vo;
    }
}
