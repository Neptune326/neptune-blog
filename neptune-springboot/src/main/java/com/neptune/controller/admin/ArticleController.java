package com.neptune.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.neptune.annotation.BackLog;
import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import com.neptune.dto.ArticleDto;
import com.neptune.entity.Article;
import com.neptune.entity.ArticleTag;
import com.neptune.entity.ResponseResult;
import com.neptune.service.ArticleService;
import com.neptune.service.ArticleTagService;
import com.neptune.service.TagService;
import com.neptune.utils.CommonUtils;
import com.neptune.vo.admin.ArticleVo;
import com.neptune.vo.admin.MenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.neptune.entity.table.ArticleTableDef.ARTICLE;
import static com.neptune.entity.table.ArticleTagTableDef.ARTICLE_TAG;

@RestController
@RequestMapping("/admin/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private TagService tagService;

    @SaCheckLogin
    @BackLog(module = "文章", type = "新增",param = false)
    @PostMapping("save")
    public ResponseResult<?> save(@RequestBody @Validated(Create.class) ArticleDto dto) {
        boolean flag = articleService.saveUpdate(dto);
        return ResponseResult.success(flag);
    }

    @SaCheckLogin
    @BackLog(module = "文章", type = "修改",param = false)
    @PostMapping("update")
    public ResponseResult<?> update(@RequestBody @Validated(Update.class) ArticleDto dto) {
        boolean flag = articleService.saveUpdate(dto);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @BackLog(module = "文章", type = "置顶")
    @GetMapping("switchTop")
    public ResponseResult<?> switchTop(@RequestParam Long id) {
        Article t = articleService.getById(id);
        Assert.notNull(t, "文章不存在");
        Integer topValue = t.getIsTop();
        t.setIsTop(topValue == 1 ? 0 : 1);
        boolean flag = articleService.updateById(t);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @BackLog(module = "文章", type = "删除")
    @GetMapping("remove")
    public ResponseResult<?> remove(@RequestParam Long id) {
        boolean flag = articleService.removeById(id);
        tagService.delArticleCache(id);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @GetMapping("info")
    public ResponseResult<ArticleVo> info(@RequestParam Long id) {
        Article t = articleService.getById(id);
        String[] tagIds = articleTagService.list(QueryWrapper.create().where(ARTICLE_TAG.ARTICLE_ID.eq(id)))
                .stream()
                .map(v -> v.getTagId().toString())
                .toArray(String[]::new);
        ArticleVo vo = BeanUtil.copyProperties(t, ArticleVo.class);
        vo.setTagIds(tagIds);
        return ResponseResult.success(vo);
    }

    @SaCheckLogin
    @GetMapping("page")
    public ResponseResult<Page<ArticleVo>> page(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        Map<String, String> search = CommonUtils.createSearch(request, "title", "categoryId");
        Page<ArticleVo> page = articleService.getPage(pageNumber, pageSize, search);
        return ResponseResult.success(page);
    }

}
