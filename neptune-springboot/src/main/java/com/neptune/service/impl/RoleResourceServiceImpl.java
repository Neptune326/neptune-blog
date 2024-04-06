package com.neptune.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.RoleResource;
import com.neptune.mapper.RoleResourceMapper;
import com.neptune.service.RoleResourceService;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.neptune.entity.table.RoleResourceTableDef.ROLE_RESOURCE;

/**
 * 角色资源关联表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class RoleResourceServiceImpl extends ServiceImpl<RoleResourceMapper, RoleResource> implements RoleResourceService {

    @Override
    public String[] getResourceIds(Long roleId) {
        if (Objects.isNull(roleId)) {
            return new String[0];
        }
        return this.list(QueryWrapper.create().where(ROLE_RESOURCE.ROLE_ID.eq(roleId)))
                .stream()
                .map(t -> t.getResourceId().toString())
                .toArray(String[]::new);
    }
}
