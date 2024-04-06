package com.neptune.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.dto.RoleMenuDto;
import com.neptune.dto.RoleResourceDto;
import com.neptune.entity.Role;
import com.neptune.entity.RoleMenu;
import com.neptune.entity.RoleResource;
import com.neptune.mapper.RoleMapper;
import com.neptune.service.RoleMenuService;
import com.neptune.service.RoleResourceService;
import com.neptune.service.RoleService;
import com.neptune.vo.admin.RoleVo;
import com.neptune.vo.admin.SelectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.neptune.entity.table.RoleMenuTableDef.ROLE_MENU;
import static com.neptune.entity.table.RoleResourceTableDef.ROLE_RESOURCE;
import static com.neptune.entity.table.RoleTableDef.ROLE;

/**
 * 角色表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleResourceService roleResourceService;
    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public Page<RoleVo> getPage(Integer pageNumber, Integer pageSize, Map<String, String> search) {
        Page<RoleVo> page = new Page<>(pageNumber, pageSize);
        String name = search.get("name");
        String code = search.get("code");
        return QueryChain.of(this.mapper)
                .where(ROLE.NAME.like(name, StrUtil.isNotBlank(name)))
                .and(ROLE.CODE.like(code, StrUtil.isNotBlank(code)))
                .orderBy(ROLE.SORT.asc(), ROLE.CREATE_TIME.desc())
                .pageAs(page, RoleVo.class);
    }

    @Override
    public boolean saveResource(RoleResourceDto dto) {
        Long id = dto.getId();
        Long[] resourceIds = dto.getResourceIds();
        roleResourceService.remove(QueryWrapper.create().where(ROLE_RESOURCE.ROLE_ID.eq(id)));
        if (ObjectUtil.isEmpty(resourceIds)) {
            return true;
        }
        List<RoleResource> list = Arrays.stream(resourceIds)
                .map(resourceId -> RoleResource.builder()
                        .roleId(id)
                        .resourceId(resourceId)
                        .build()).collect(Collectors.toList());
        return roleResourceService.saveBatch(list);
    }

    @Override
    public boolean saveMenu(RoleMenuDto dto) {
        Long id = dto.getId();
        Long[] menuIds = dto.getMenuIds();
        roleMenuService.remove(QueryWrapper.create().where(ROLE_MENU.ROLE_ID.eq(id)));
        if (ObjectUtil.isEmpty(menuIds)) {
            return true;
        }
        List<RoleMenu> list = Arrays.stream(menuIds)
                .map(menuId -> RoleMenu.builder()
                        .roleId(id)
                        .menuId(menuId)
                        .build()).collect(Collectors.toList());
        return roleMenuService.saveBatch(list);
    }

    @Override
    public List<RoleVo> getList() {
        return QueryChain.of(this.mapper)
                .orderBy(ROLE.SORT.asc(), ROLE.CREATE_TIME.desc())
                .listAs(RoleVo.class);
    }

    @Override
    public List<SelectVo> getSelectList() {
        return QueryChain.of(this.mapper)
                .select(ROLE.ID.as(SelectVo::getValue), ROLE.NAME.as(SelectVo::getLabel))
                .orderBy(ROLE.SORT.asc(), ROLE.CREATE_TIME.desc())
                .listAs(SelectVo.class);
    }
}
