package com.neptune.service.impl;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.Menu;
import com.neptune.entity.Resource;
import com.neptune.mapper.ResourceMapper;
import com.neptune.service.MenuService;
import com.neptune.service.ResourceService;
import com.neptune.vo.admin.ResourceVo;
import com.neptune.vo.admin.SelectVo;
import com.neptune.vo.admin.TreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.neptune.entity.table.MenuTableDef.MENU;
import static com.neptune.entity.table.ResourceTableDef.RESOURCE;

/**
 * 权限资源表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @Autowired
    private MenuService menuService;

    @Override
    public Page<ResourceVo> getPage(Integer pageNumber, Integer pageSize, Map<String, String> search) {
        Page<ResourceVo> page = new Page<>(pageNumber, pageSize);
        String name = search.get("name");
        String menuId = search.get("menuId");
        return QueryChain.of(this.mapper)
                .select(RESOURCE.ALL_COLUMNS)
                .from(RESOURCE)
                .leftJoin(MENU).on(RESOURCE.MENU_ID.eq(MENU.ID))
                .where(RESOURCE.NAME.like(name, StrUtil.isNotBlank(name)))
                .and(MENU.MENU_ID_PATH.like(menuId, StrUtil.isNotBlank(menuId)))
                .orderBy(RESOURCE.SORT.asc(), RESOURCE.CREATE_TIME.desc())
                .pageAs(page, ResourceVo.class);
    }

    @Override
    public boolean saveUpdate(Resource t) {
        Long menuId = t.getMenuId();
        Menu menu = menuService.getById(menuId);
        t.setMenuName(menu.getName());
        t.setFullCode(menu.getCode() + ":" + t.getCode());
        if (Objects.isNull(t.getId())) {
            return this.save(t);
        }
        return this.updateById(t, false);
    }

    @Override
    public List<TreeVo> getTree() {
        List<Resource> resources = this.list(QueryWrapper.create().orderBy(RESOURCE.SORT.asc(), RESOURCE.CREATE_TIME.desc()));
        List<Menu> menus = menuService.getList();
        List<TreeVo> treeVos = buildTreeVo(menus, resources, null);
        return Lists.newArrayList(
                TreeVo.builder()
                        .label("顶级菜单")
                        .icon("format_align_justify")
                        .children(treeVos)
                        .build()
        );
    }

    public List<TreeVo> buildTreeVo(List<Menu> menus, List<Resource> resources, Long menuId) {
        List<TreeVo> list = resources.stream()
                .filter(t -> Objects.equals(menuId, t.getMenuId()))
                .map(t -> TreeVo.builder()
                        .label(t.getName())
                        .value(t.getId().toString())
                        .icon("check")
                        .build()).collect(Collectors.toList());
        List<TreeVo> menuList = menus.stream()
                .filter(t -> Objects.equals(menuId, t.getParentId()))
                .map(t -> TreeVo.builder()
                        .label(t.getName())
                        .value(t.getId().toString())
                        .icon("format_align_justify")
                        .children(buildTreeVo(menus, resources, t.getId()))
                        .build()).collect(Collectors.toList());
        list.addAll(menuList);
        return list;
    }

    @Override
    public List<?> getList(boolean select) {
        List<ResourceVo> list = QueryChain.of(this.mapper)
                .select(RESOURCE.ID, RESOURCE.NAME)
                .orderBy(RESOURCE.SORT.asc(), RESOURCE.CREATE_TIME.desc())
                .listAs(ResourceVo.class);
        if (select) {
            return list.stream().map(t -> SelectVo.create(t.getName(), t.getId())).collect(Collectors.toList());
        }
        return list;
    }
}
