package com.neptune.controller.blog;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.neptune.entity.Article;
import com.neptune.entity.ResponseResult;
import com.neptune.service.ArticleService;
import com.neptune.service.CategoryService;
import com.neptune.utils.CommonUtils;
import com.neptune.vo.blog.BlogArticlePageVo;
import com.neptune.vo.blog.BlogArticleViewVo;
import com.neptune.vo.blog.BlogCategoryVo;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.neptune.entity.table.ArticleTableDef.ARTICLE;
import static com.neptune.entity.table.CategoryTableDef.CATEGORY;


@RestController
@RequestMapping("/blog/article")
public class BlogArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("page")
    public ResponseResult<Page<BlogArticlePageVo>> page(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String title,
            Long categoryId
    ) {
        Page<BlogArticlePageVo> page = new Page<>(pageNumber, pageSize);
        articleService.pageAs(
                page,
                QueryWrapper.create()
                        .where(ARTICLE.TITLE.like(title,StrUtil::isNotBlank))
                        .where(ARTICLE.CATEGORY_ID.eq(categoryId, Objects::nonNull))
                        .orderBy(ARTICLE.IS_TOP.desc(),ARTICLE.CREATE_TIME.desc()),
                BlogArticlePageVo.class
        );
        return ResponseResult.success(page);
    }

    @GetMapping("categoryList")
    public ResponseResult<List<BlogCategoryVo>> categoryList( ) {
        List<BlogCategoryVo> list = categoryService.listAs(
                QueryWrapper.create().orderBy(CATEGORY.SORT.asc(), CATEGORY.CREATE_TIME.desc()),
                BlogCategoryVo.class
        );
        return ResponseResult.success(list);
    }

    @GetMapping("info")
    public ResponseResult<BlogArticleViewVo> info(@RequestParam Long id) {
        BlogArticleViewVo vo = articleService.getBlogViewInfo(id);
        return ResponseResult.success(vo);
    }

}
