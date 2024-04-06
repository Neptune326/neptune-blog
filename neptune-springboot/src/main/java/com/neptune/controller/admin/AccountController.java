package com.neptune.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.neptune.annotation.BackLog;
import com.neptune.dto.LoginDto;
import com.neptune.entity.ResponseResult;
import com.neptune.entity.User;
import com.neptune.service.MenuService;
import com.neptune.service.RoleService;
import com.neptune.service.UserService;
import com.neptune.utils.PasswordUtils;
import com.neptune.vo.admin.LoginInfoVo;
import com.neptune.vo.admin.MenuVo;
import com.neptune.vo.admin.RouteVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.neptune.entity.table.UserTableDef.USER;

@RestController
@RequestMapping("/admin/account")
public class AccountController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;

    @BackLog(module = "账号", type = "登录")
    @PostMapping("login")
    public ResponseResult<LoginInfoVo> login(@RequestBody @Validated LoginDto dto) {
        User user = userService.getOne(QueryWrapper.create().where(USER.USER_NAME.eq(dto.getUserName())));
        if (user == null) {
            throw new NotRoleException("用户不存在");
        }
        if (!PasswordUtils.verify(dto.getPassword(), user.getSalt(), user.getPassword())) {
            throw new NotRoleException("用户名或密码错误");
        }
        StpUtil.login(user.getId());
        LoginInfoVo loginInfoVo = BeanUtil.copyProperties(user, LoginInfoVo.class);
        loginInfoVo.setUserId(user.getId().toString());
        loginInfoVo.setToken(StpUtil.getTokenValue());
        return ResponseResult.success(loginInfoVo);
    }

    @BackLog(module = "账号", type = "注销")
    @PostMapping("logout")
    public ResponseResult<?> logout() {
        StpUtil.logout();
        return ResponseResult.success();
    }

    @SaCheckLogin
    @BackLog(module = "账号", type = "菜单")
    @PostMapping("menus")
    public ResponseResult<Map<String, Object>> menus() {
        List<MenuVo> menus = userService.getUserMenus();
        List<RouteVo> routes = userService.getUserRoutes();
        Map<String, Object> map = MapUtil.<String, Object>builder()
                .put("menus", menus)
                .put("routes", routes)
                .build();
        return ResponseResult.success(map);
    }

}
