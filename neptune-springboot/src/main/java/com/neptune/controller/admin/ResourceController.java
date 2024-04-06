package com.neptune.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.neptune.annotation.BackLog;
import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import com.neptune.dto.ResourceDto;
import com.neptune.entity.Resource;
import com.neptune.entity.ResponseResult;
import com.neptune.service.MenuService;
import com.neptune.service.ResourceService;
import com.neptune.utils.CommonUtils;
import com.neptune.vo.admin.ResourceEditVo;
import com.neptune.vo.admin.ResourceVo;
import com.neptune.vo.admin.TreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.neptune.entity.table.ResourceTableDef.RESOURCE;

@RestController
@RequestMapping("/admin/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private MenuService menuService;

    @SaCheckLogin
    @BackLog(module = "资源", type = "新增")
    @PostMapping("save")
    public ResponseResult<?> save(@RequestBody @Validated(Create.class) ResourceDto dto) {
        Resource t = BeanUtil.copyProperties(dto, Resource.class);
        boolean flag = resourceService.saveUpdate(t);
        return ResponseResult.success(flag);
    }

    @SaCheckLogin
    @BackLog(module = "资源", type = "修改")
    @PostMapping("update")
    public ResponseResult<?> update(@RequestBody @Validated(Update.class) ResourceDto dto) {
        Resource t = resourceService.getById(dto.getId());
        Assert.notNull(t);
        BeanUtil.copyProperties(dto, t);
        boolean flag = resourceService.saveUpdate(t);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @BackLog(module = "资源", type = "删除")
    @GetMapping("remove")
    public ResponseResult<?> remove(@RequestParam Long id) {
        boolean flag = resourceService.removeById(id);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @GetMapping("editInfo")
    public ResponseResult<ResourceEditVo> editInfo(@RequestParam Long id) {
        Resource t = resourceService.getById(id);
        return ResponseResult.success(BeanUtil.copyProperties(t, ResourceEditVo.class));
    }

    @SaCheckLogin
    @GetMapping("info")
    public ResponseResult<ResourceVo> info(@RequestParam Long id) {
        Resource t = resourceService.getById(id);
        return ResponseResult.success(BeanUtil.copyProperties(t, ResourceVo.class));
    }

    @SaCheckLogin
    @GetMapping("list")
    public ResponseResult<List<?>> list(boolean select) {
        List<?> list = resourceService.getList(select);
        return ResponseResult.success(list);
    }

    @SaCheckLogin
    @GetMapping("page")
    public ResponseResult<Page> page(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        Map<String, String> search = CommonUtils.createSearch(request, "name", "menuId");
        Page<ResourceVo> page = resourceService.getPage(pageNumber, pageSize, search);
        return ResponseResult.success(page);
    }

    @SaCheckPermission(value = {"resource:add", "resource:update"}, mode = SaMode.OR)
    @GetMapping("check")
    public ResponseResult<?> check(Long id, Long menuId, String name, String code) {
        QueryWrapper qw = QueryChain.create().where(RESOURCE.ID.ne(id, Objects.nonNull(id))).and(RESOURCE.MENU_ID.eq(menuId));
        if (StrUtil.isNotBlank(name)) {
            long count = resourceService.count(qw.and(RESOURCE.NAME.eq(name)));
            return ResponseResult.result(count == 0, "资源名称已存在");
        }
        if (StrUtil.isNotBlank(code)) {
            long count = resourceService.count(qw.and(RESOURCE.CODE.eq(code)));
            return ResponseResult.result(count == 0, "资源编码已存在");
        }
        return ResponseResult.success();
    }

    @SaCheckLogin
    @GetMapping("tree")
    public ResponseResult<List<TreeVo>> tree() {
        List<TreeVo> list = resourceService.getTree();
        return ResponseResult.success(list);
    }

    @SaCheckLogin
    @GetMapping("menuTree")
    public ResponseResult<List<TreeVo>> menuTree() {
        List<TreeVo> list = menuService.getTree();
        return ResponseResult.success(list);
    }

}
