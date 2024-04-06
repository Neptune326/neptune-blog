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
import com.neptune.dto.MenuDto;
import com.neptune.entity.Menu;
import com.neptune.entity.ResponseResult;
import com.neptune.service.MenuService;
import com.neptune.utils.CommonUtils;
import com.neptune.vo.admin.MenuEditVo;
import com.neptune.vo.admin.MenuVo;
import com.neptune.vo.admin.SelectVo;
import com.neptune.vo.admin.TreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.neptune.entity.table.MenuTableDef.MENU;

@RestController
@RequestMapping("/admin/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @SaCheckLogin
    @BackLog(module = "菜单", type = "新增")
    @PostMapping("save")
    public ResponseResult<?> save(@RequestBody @Validated(Create.class) MenuDto dto) {
        Menu t = BeanUtil.copyProperties(dto, Menu.class);
        boolean flag = menuService.saveUpdate(t);
        return ResponseResult.success(flag);
    }

    @SaCheckLogin
    @BackLog(module = "菜单", type = "修改")
    @PostMapping("update")
    public ResponseResult<?> update(@RequestBody @Validated(Update.class) MenuDto dto) {
        Menu t = menuService.getById(dto.getId());
        Assert.notNull(t);
        BeanUtil.copyProperties(dto, t);
        boolean flag = menuService.saveUpdate(t);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @BackLog(module = "菜单", type = "切换显示")
    @GetMapping("switchVisible")
    public ResponseResult<?> switchVisible(@RequestParam Long id) {
        Menu t = menuService.getById(id);
        Assert.notNull(t);
        Integer isHidden = t.getIsHidden();
        t.setIsHidden(isHidden == 1 ? 0 : 1);
        boolean flag = menuService.updateById(t);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @BackLog(module = "菜单", type = "删除")
    @GetMapping("remove")
    public ResponseResult<?> remove(@RequestParam Long id) {
        boolean flag = menuService.removeById(id);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @GetMapping("editInfo")
    public ResponseResult<MenuEditVo> editInfo(@RequestParam Long id) {
        Menu t = menuService.getById(id);
        return ResponseResult.success(BeanUtil.copyProperties(t, MenuEditVo.class));
    }

    @SaCheckLogin
    @GetMapping("info")
    public ResponseResult<MenuVo> info(@RequestParam Long id) {
        Menu t = menuService.getById(id);
        return ResponseResult.success(BeanUtil.copyProperties(t, MenuVo.class));
    }

    @SaCheckLogin
    @GetMapping("list")
    public ResponseResult<List<MenuVo>> list() {
        List<MenuVo> list = menuService.getListVo();
        return ResponseResult.success(list);
    }

    @SaCheckLogin
    @GetMapping("selectList")
    public ResponseResult<List<SelectVo>> selectList() {
        List<SelectVo> list = menuService.getSelectVo();
        return ResponseResult.success(list);
    }

    @SaCheckLogin
    @GetMapping("listAll")
    public ResponseResult<List<MenuVo>> listAll(HttpServletRequest request) {
        Map<String, String> search = CommonUtils.createSearch(request, "name", "parentId");
        List<MenuVo> list = menuService.listAll(search);
        return ResponseResult.success(list);
    }

    @SaCheckLogin
    @GetMapping("page")
    public ResponseResult<Page<MenuVo>> page(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        Map<String, String> search = CommonUtils.createSearch(request, "name", "parentId");
        Page<MenuVo> page = menuService.getPage(pageNumber, pageSize, search);
        return ResponseResult.success(page);
    }


    @SaCheckLogin
    @GetMapping("check")
    public ResponseResult<?> check(Long id, String name, String code) {
        QueryWrapper qw = QueryChain.create().where(MENU.ID.ne(id, Objects.nonNull(id)));
        if (StrUtil.isNotBlank(name)) {
            long count = menuService.count(qw.and(MENU.NAME.eq(name)));
            return ResponseResult.result(count == 0, "菜单名称已存在");
        }
        if (StrUtil.isNotBlank(code)) {
            long count = menuService.count(qw.and(MENU.CODE.eq(code)));
            return ResponseResult.result(count == 0, "菜单编码已存在");
        }
        return ResponseResult.success();
    }


    @SaCheckLogin
    @GetMapping("tree")
    public ResponseResult<List<TreeVo>> tree() {
        List<TreeVo> list = menuService.getTree();
        return ResponseResult.success(list);
    }

}
