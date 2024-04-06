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
import com.neptune.dto.RoleDto;
import com.neptune.dto.RoleMenuDto;
import com.neptune.dto.RoleResourceDto;
import com.neptune.entity.ResponseResult;
import com.neptune.entity.Role;
import com.neptune.service.*;
import com.neptune.utils.CommonUtils;
import com.neptune.vo.admin.RoleEditVo;
import com.neptune.vo.admin.RoleVo;
import com.neptune.vo.admin.SelectVo;
import com.neptune.vo.admin.TreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.neptune.entity.table.RoleTableDef.ROLE;

@RestController
@RequestMapping("/admin/role")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleResourceService roleResourceService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private ResourceService resourceService;

    @SaCheckLogin
    @BackLog(module = "角色", type = "新增")
    @PostMapping("save")
    public ResponseResult<String> save(@RequestBody @Validated(Create.class) RoleDto dto) {
        Role t = BeanUtil.copyProperties(dto, Role.class);
        boolean flag = roleService.save(t);
        return ResponseResult.success("保存成功", t.getId().toString());
    }

    @SaCheckLogin
    @BackLog(module = "角色", type = "修改")
    @PostMapping("update")
    public ResponseResult<?> update(@RequestBody @Validated(Update.class) RoleDto dto) {
        Role t = roleService.getById(dto.getId());
        Assert.notNull(t);
        BeanUtil.copyProperties(dto, t);
        roleService.updateById(t, false);
        return ResponseResult.success("更新成功", t.getId().toString());
    }

    @SaCheckLogin
    @BackLog(module = "角色", type = "删除")
    @GetMapping("remove")
    public ResponseResult<?> remove(@RequestParam Long id) {
        boolean flag = roleService.removeById(id);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @GetMapping("editInfo")
    public ResponseResult<RoleEditVo> editInfo(@RequestParam Long id) {
        Role t = roleService.getById(id);
        RoleEditVo vo = BeanUtil.copyProperties(t, RoleEditVo.class);
        vo.setMenuIds(roleMenuService.getMenuIds(id));
        vo.setResourceIds(roleResourceService.getResourceIds(id));
        return ResponseResult.success(vo);
    }

    @SaCheckLogin
    @GetMapping("info")
    public ResponseResult<RoleVo> info(@RequestParam Long id) {
        Role t = roleService.getById(id);
        RoleVo vo = BeanUtil.copyProperties(t, RoleVo.class);
        vo.setResourceIds(roleResourceService.getResourceIds(id));
        return ResponseResult.success(vo);
    }

    @SaCheckLogin
    @GetMapping("list")
    public ResponseResult<List<RoleVo>> list() {
        List<RoleVo> list = roleService.getList();
        return ResponseResult.success(list);
    }

    @SaCheckLogin
    @GetMapping("selectList")
    public ResponseResult<List<SelectVo>> selectList() {
        List<SelectVo> list = roleService.getSelectList();
        return ResponseResult.success(list);
    }

    @SaCheckLogin
    @GetMapping("page")
    public ResponseResult<Page> page(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        Map<String, String> search = CommonUtils.createSearch(request, "name", "code");
        Page<RoleVo> page = roleService.getPage(pageNumber, pageSize, search);
        return ResponseResult.success(page);
    }


    @SaCheckLogin
    @GetMapping("check")
    public ResponseResult<?> check(Long id, String name, String code) {
        QueryWrapper qw = QueryChain.create().where(ROLE.ID.ne(id, Objects.nonNull(id)));
        if (StrUtil.isNotBlank(name)) {
            long count = roleService.count(qw.and(ROLE.NAME.eq(name)));
            return ResponseResult.result(count == 0, "角色名称已存在");
        }
        if (StrUtil.isNotBlank(code)) {
            long count = roleService.count(qw.and(ROLE.CODE.eq(code)));
            return ResponseResult.result(count == 0, "角色编码已存在");
        }
        return ResponseResult.success();
    }

    @SaCheckLogin
    @BackLog(module = "角色", type = "保存菜单")
    @PostMapping("saveMenu")
    public ResponseResult<?> saveMenu(@RequestBody RoleMenuDto dto) {
        boolean flag = roleService.saveMenu(dto);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @BackLog(module = "角色", type = "保存资源")
    @PostMapping("saveResource")
    public ResponseResult<?> saveResource(@RequestBody RoleResourceDto dto) {
        boolean flag = roleService.saveResource(dto);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @GetMapping("menuTree")
    public ResponseResult<List<TreeVo>> menuTree() {
        List<TreeVo> list = menuService.getTree();
        return ResponseResult.success(list);
    }

    @SaCheckLogin
    @GetMapping("resourceTree")
    public ResponseResult<List<TreeVo>> resourceTree() {
        List<TreeVo> list = resourceService.getTree();
        return ResponseResult.success(list);
    }

}
