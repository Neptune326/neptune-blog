package com.neptune.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.neptune.dto.SelfUserDto;
import com.neptune.dto.UserDto;
import com.neptune.entity.User;
import com.neptune.vo.admin.MenuVo;
import com.neptune.vo.admin.RouteVo;
import com.neptune.vo.admin.UserVo;

import java.util.List;
import java.util.Map;

/**
 * 用户表 服务层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public interface UserService extends IService<User> {

    Page<UserVo> getPage(Integer pageNumber, Integer pageSize, Map<String, String> search);

    User saveUpdate(UserDto dto);

    List<MenuVo> getUserMenus();

    List<RouteVo> getUserRoutes();

    void updateSelfInfo(SelfUserDto dto);
}
