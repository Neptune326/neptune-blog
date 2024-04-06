package com.neptune.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.RoleMenu;
import com.neptune.mapper.RoleMenuMapper;
import com.neptune.service.RoleMenuService;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.neptune.entity.table.RoleMenuTableDef.ROLE_MENU;

/**
 * 角色资源关联表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Override
    public String[] getMenuIds(Long roleId) {
        if (Objects.isNull(roleId)) {
            return new String[0];
        }
        return this.list(QueryWrapper.create().where(ROLE_MENU.ROLE_ID.eq(roleId)))
                .stream()
                .map(t -> t.getMenuId().toString())
                .toArray(String[]::new);
    }
}
