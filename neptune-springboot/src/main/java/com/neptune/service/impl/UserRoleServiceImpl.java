package com.neptune.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.UserRole;
import com.neptune.mapper.UserRoleMapper;
import com.neptune.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户角色关联表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
