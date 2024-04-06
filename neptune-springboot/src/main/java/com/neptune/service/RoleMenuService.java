package com.neptune.service;

import com.mybatisflex.core.service.IService;
import com.neptune.entity.RoleMenu;

/**
 * 角色资源关联表 服务层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public interface RoleMenuService extends IService<RoleMenu> {

    String[] getMenuIds(Long roleId);
}
