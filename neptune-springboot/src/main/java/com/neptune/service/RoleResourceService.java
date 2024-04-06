package com.neptune.service;

import com.mybatisflex.core.service.IService;
import com.neptune.entity.RoleResource;

/**
 * 角色资源关联表 服务层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public interface RoleResourceService extends IService<RoleResource> {

    String[] getResourceIds(Long roleId);

}
