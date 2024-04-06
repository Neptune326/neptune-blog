package com.neptune.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.neptune.dto.RoleMenuDto;
import com.neptune.dto.RoleResourceDto;
import com.neptune.entity.Role;
import com.neptune.vo.admin.RoleVo;
import com.neptune.vo.admin.SelectVo;

import java.util.List;
import java.util.Map;

/**
 * 角色表 服务层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public interface RoleService extends IService<Role> {

    List<RoleVo> getList();

    List<SelectVo> getSelectList();

    Page<RoleVo> getPage(Integer pageNumber, Integer pageSize, Map<String, String> search);

    boolean saveResource(RoleResourceDto dto);

    boolean saveMenu(RoleMenuDto dto);
}
