package com.neptune.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.Menu;
import com.neptune.mapper.MenuMapper;
import com.neptune.service.MenuService;
import com.neptune.vo.admin.MenuVo;
import com.neptune.vo.admin.SelectVo;
import com.neptune.vo.admin.TreeVo;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.neptune.entity.table.MenuTableDef.MENU;

/**
 * 菜单表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public Page<MenuVo> getPage(Integer pageNumber, Integer pageSize, Map<String, String> search) {
        Page<MenuVo> page = new Page<>(pageNumber, pageSize);
        String name = search.get("name");
        String parentId = search.get("parentId");
        return QueryChain.of(this.mapper)
                .where(MENU.NAME.like(name, StrUtil.isNotBlank(name)))
                .and(MENU.PARENT_ID.eq(parentId, StrUtil.isNotBlank(parentId)))
                .orderBy(MENU.SORT.asc(), MENU.CREATE_TIME.desc())
                .pageAs(page, MenuVo.class)
                .map(v -> {
                    String pId = v.getParentId();
                    if (StrUtil.isNotBlank(pId)) {
                        Menu parent = this.getById(pId);
                        if (Objects.nonNull(parent)) {
                            v.setParentName(parent.getName());
                        }
                    }
                    return v;
                });
    }

    @Override
    public List<Menu> getList() {
        return this.list(QueryWrapper.create().orderBy(MENU.SORT.asc(), MENU.CREATE_TIME.desc()));
    }

    public List<MenuVo> listVo() {
        return this.listAs(QueryWrapper.create().orderBy(MENU.SORT.asc(), MENU.CREATE_TIME.desc()), MenuVo.class);
    }

    @Override
    public List<MenuVo> getListVo() {
        return this.listVo();
    }

    @Override
    public List<SelectVo> getSelectVo() {
        List<MenuVo> list = this.listVo();
        return buildSelectVo(list, null);
    }

    public List<SelectVo> buildSelectVo(List<MenuVo> list, String rootId) {
        if (list.isEmpty()) {
            return Lists.newArrayList();
        }
        return list.stream()
                .filter(t -> {
                    if (StrUtil.isBlank(rootId)) {
                        return Objects.isNull(t.getParentId());
                    }
                    return rootId.equals(t.getParentId());
                })
                .map(t -> SelectVo.create(t.getName(), t.getId()))
                .flatMap(t -> {
                    List<SelectVo> children = buildSelectVo(list, t.getValue());
                    children.add(0, t);
                    return children.stream();
                }).collect(Collectors.toList());
    }

    @Override
    public List<TreeVo> getTree() {
        List<Menu> list = this.list(QueryWrapper.create().orderBy(MENU.SORT.asc(), MENU.CREATE_TIME.desc()));
        return buildTreeVo(list, null);
    }

    @Override
    public boolean saveUpdate(Menu t) {
        if (Objects.isNull(t.getParentId())) {
            t.setLevel(1);
        } else {
            Menu parent = this.getById(t.getParentId());
            if (Objects.nonNull(parent)) {
                t.setLevel(parent.getLevel() + 1);
            }
        }
        if (t.getType() == 1) {
            t.setRoute(null);
            t.setComponentName(null);
            t.setComponentPath(null);
        }
        if (Objects.isNull(t.getId())) {
            this.save(t);
        }
        List<Menu> fullList = Lists.newArrayList();
        getParentMenu(t, fullList);
        String menuIdPath = "", menuNamePath = "";
        if (CollUtil.isNotEmpty(fullList)) {
            menuIdPath = fullList.stream().map(Menu::getId).map(String::valueOf).collect(Collectors.joining("$", "$", "$"));
            menuNamePath = fullList.stream().map(Menu::getName).collect(Collectors.joining("/"));
        }
        t.setMenuIdPath(menuIdPath);
        t.setMenuNamePath(menuNamePath);
        return this.updateById(t, false);
    }

    //递归获取所有父菜单
    public void getParentMenu(Menu menu, List<Menu> list) {
        list.add(0, menu);
        if (Objects.nonNull(menu.getParentId())) {
            Menu parent = this.getById(menu.getParentId());
            if (Objects.nonNull(parent)) {
                getParentMenu(parent, list);
            }
        }
    }


    @Override
    public List<MenuVo> listAll(Map<String, String> search) {
        return menuMapper.listAll(search);
    }

    @Override
    public boolean updateVisibleStatus(Long id, Integer isHidden) {
        Menu t = this.getById(id);
        if (Objects.nonNull(t)) {
            t.setIsHidden(isHidden);
            return this.updateById(t);
        }
        return false;
    }

    public List<TreeVo> buildTreeVo(List<Menu> list, Long menuId) {
        if (list.isEmpty()) {
            return Lists.newArrayList();
        }
        return list.stream()
                .filter(t -> Objects.equals(menuId, t.getParentId()))
                .map(t ->
                        TreeVo.builder()
                                .label(t.getName())
                                .value(t.getId().toString())
                                .icon(t.getIcon())
                                .children(buildTreeVo(list, t.getId()))
                                .build()
                ).collect(Collectors.toList());
    }
}
