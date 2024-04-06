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
import com.neptune.dto.TagDto;
import com.neptune.entity.ResponseResult;
import com.neptune.entity.Tag;
import com.neptune.service.TagService;
import com.neptune.vo.admin.TagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.neptune.entity.table.TagTableDef.TAG;

@RestController
@RequestMapping("/admin/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @SaCheckLogin
    @BackLog(module = "标签", type = "新增")
    @PostMapping("save")
    public ResponseResult<?> save(@RequestBody @Validated(Create.class) TagDto dto) {
        Tag t = BeanUtil.copyProperties(dto, Tag.class);
        t.setCreateTime(LocalDateTime.now());
        boolean flag = tagService.save(t);
        tagService.updateTagCache();
        return ResponseResult.success(flag);
    }

    @SaCheckLogin
    @BackLog(module = "标签", type = "修改")
    @PostMapping("update")
    public ResponseResult<?> update(@RequestBody @Validated(Update.class) TagDto dto) {
        Tag t = tagService.getById(dto.getId());
        Assert.notNull(t);
        BeanUtil.copyProperties(dto, t);
        boolean flag = tagService.updateById(t);
        tagService.updateTagCache();
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @BackLog(module = "标签", type = "删除")
    @GetMapping("remove")
    public ResponseResult<?> remove(@RequestParam Long id) {
        boolean flag = tagService.removeById(id);
        tagService.updateTagCache();
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @GetMapping("info")
    public ResponseResult<TagVo> info(@RequestParam Long id) {
        Tag t = tagService.getById(id);
        return ResponseResult.success(BeanUtil.copyProperties(t, TagVo.class));
    }

    @SaCheckLogin
    @GetMapping("list")
    public ResponseResult<List<TagVo>> list() {
        List<TagVo> list = tagService.listAs(QueryWrapper.create().orderBy(TAG.SORT.asc(), TAG.CREATE_TIME.desc()), TagVo.class);
        return ResponseResult.success(list);
    }

    @SaCheckLogin
    @GetMapping("page")
    public ResponseResult<Page<TagVo>> page(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name
    ) {
        Page<TagVo> page = new Page<>(pageNumber, pageSize);
        tagService.pageAs(
                page,
                QueryWrapper.create()
                        .where(TAG.NAME.like(name, StrUtil.isNotBlank(name)))
                        .orderBy(TAG.SORT.asc(), TAG.CREATE_TIME.desc()),
                TagVo.class
        );
        return ResponseResult.success(page);
    }


    @SaCheckLogin
    @GetMapping("check")
    public ResponseResult<?> check(Long id, String name) {
        QueryWrapper qw = QueryChain.create().where(TAG.ID.ne(id, Objects.nonNull(id)));
        if (StrUtil.isNotBlank(name)) {
            long count = tagService.count(qw.and(TAG.NAME.eq(name)));
            return ResponseResult.result(count == 0, "分类已存在");
        }
        return ResponseResult.success();
    }

}
