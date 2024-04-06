package com.neptune.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.neptune.annotation.BackLog;
import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import com.neptune.dto.CategoryDto;
import com.neptune.entity.Category;
import com.neptune.entity.ResponseResult;
import com.neptune.service.CategoryService;
import com.neptune.vo.admin.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.neptune.entity.table.CategoryTableDef.CATEGORY;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @SaCheckLogin
    @BackLog(module = "分类", type = "新增")
    @PostMapping("save")
    public ResponseResult<?> save(@RequestBody @Validated(Create.class) CategoryDto dto) {
        Category t = BeanUtil.copyProperties(dto, Category.class);
        t.setCreateTime(LocalDateTime.now());
        boolean flag = categoryService.save(t);
        return ResponseResult.success(flag);
    }

    @SaCheckLogin
    @BackLog(module = "分类", type = "修改")
    @PostMapping("update")
    public ResponseResult<?> update(@RequestBody @Validated(Update.class) CategoryDto dto) {
        Category t = categoryService.getById(dto.getId());
        Assert.notNull(t);
        BeanUtil.copyProperties(dto, t);
        boolean flag = categoryService.updateById(t);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @BackLog(module = "分类", type = "删除")
    @GetMapping("remove")
    public ResponseResult<?> remove(@RequestParam Long id) {
        boolean flag = categoryService.removeById(id);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @GetMapping("info")
    public ResponseResult<CategoryVo> info(@RequestParam Long id) {
        Category t = categoryService.getById(id);
        return ResponseResult.success(BeanUtil.copyProperties(t, CategoryVo.class));
    }

    @SaCheckLogin
    @GetMapping("list")
    public ResponseResult<List<CategoryVo>> list() {
        List<CategoryVo> list = categoryService.listAs(QueryWrapper.create().orderBy(CATEGORY.SORT.asc(), CATEGORY.CREATE_TIME.desc()), CategoryVo.class);
        return ResponseResult.success(list);
    }

    @SaCheckLogin
    @GetMapping("page")
    public ResponseResult<Page<CategoryVo>> page(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name
    ) {
        Page<CategoryVo> page = new Page<>(pageNumber, pageSize);
        categoryService.pageAs(
                page,
                QueryWrapper.create()
                        .where(CATEGORY.NAME.like(name, StrUtil.isNotBlank(name)))
                        .orderBy(CATEGORY.SORT.asc(), CATEGORY.CREATE_TIME.desc()),
                CategoryVo.class
        );
        return ResponseResult.success(page);
    }


    @SaCheckLogin
    @GetMapping("check")
    public ResponseResult<?> check(Long id, String name) {
        QueryWrapper qw = QueryChain.create().where(CATEGORY.ID.ne(id, Objects.nonNull(id)));
        if (StrUtil.isNotBlank(name)) {
            long count = categoryService.count(qw.and(CATEGORY.NAME.eq(name)));
            return ResponseResult.result(count == 0, "分类已存在");
        }
        return ResponseResult.success();
    }

}
