package com.neptune.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.dto.SelfUserDto;
import com.neptune.dto.UserDto;
import com.neptune.entity.Menu;
import com.neptune.entity.User;
import com.neptune.entity.UserRole;
import com.neptune.mapper.UserMapper;
import com.neptune.service.MenuService;
import com.neptune.service.UserRoleService;
import com.neptune.service.UserService;
import com.neptune.utils.PasswordUtils;
import com.neptune.vo.admin.MenuVo;
import com.neptune.vo.admin.RouteVo;
import com.neptune.vo.admin.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.neptune.entity.table.MenuTableDef.MENU;
import static com.neptune.entity.table.RoleMenuTableDef.ROLE_MENU;
import static com.neptune.entity.table.UserRoleTableDef.USER_ROLE;
import static com.neptune.entity.table.UserTableDef.USER;

/**
 * 用户表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private MenuService menuService;

    @Override
    public Page<UserVo> getPage(Integer pageNumber, Integer pageSize, Map<String, String> search) {
        Page<UserVo> page = new Page<>(pageNumber, pageSize);
        String userName = search.get("userName");
        String nickName = search.get("nickName");
        return QueryChain.of(this.mapper)
                .where(USER.USER_NAME.like(userName, StrUtil.isNotBlank(userName)))
                .and(USER.NICK_NAME.like(nickName, StrUtil.isNotBlank(nickName)))
                .orderBy(USER.CREATE_TIME.desc())
                .pageAs(page, UserVo.class);
    }

    @Override
    public User saveUpdate(UserDto dto) {
        User t;
        if (Objects.isNull(dto.getId())) {
            t = BeanUtil.copyProperties(dto, User.class);
            String salt = PasswordUtils.generateSalt();
            String hashPassword = PasswordUtils.hashPassword(t.getPassword(), salt);
            t.setSalt(salt);
            t.setPassword(hashPassword);
            this.save(t);
        } else {
            t = this.getById(dto.getId());
            Assert.notNull(t);
            BeanUtil.copyProperties(dto, t);
            this.updateById(t);
        }

        userRoleService.remove(QueryWrapper.create().where(USER_ROLE.USER_ID.eq(t.getId())));
        if (ObjectUtil.isNotEmpty(dto.getRoleIds())) {
            List<UserRole> userRoleList = Arrays.stream(dto.getRoleIds()).map(roleId ->
                    UserRole.builder().roleId(roleId).userId(t.getId()).build()
            ).collect(Collectors.toList());
            userRoleService.saveBatch(userRoleList);
        }
        return t;
    }

    public List<Long> getCurrentUserRoleIds() {
        long userId = StpUtil.getLoginIdAsLong();
        return userRoleService.list(QueryWrapper.create().select(USER_ROLE.ROLE_ID).where(USER_ROLE.USER_ID.eq(userId)))
                .stream().map(UserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    public List<MenuVo> getUserMenus() {
        List<Long> roleIds = getCurrentUserRoleIds();
        QueryWrapper qw = QueryWrapper.create()
                .select(MENU.ALL_COLUMNS)
                .from(MENU)
                .where(MENU.ID.in(
                        QueryWrapper.create()
                                .select(ROLE_MENU.MENU_ID)
                                .from(ROLE_MENU)
                                .where(ROLE_MENU.ROLE_ID.in(roleIds))
                )).orderBy(MENU.SORT.asc(), MENU.CREATE_TIME.desc());
        List<Menu> list = menuService.list(qw);
        //找到所有的父级菜单 添加到其中
        list = addParentMenu(list);
        List<Long> menuIds = list.stream().map(Menu::getId).filter(Objects::nonNull).collect(Collectors.toList());
        List<MenuVo> result = menuService.listAs(
                QueryWrapper.create()
                        .select(MENU.ALL_COLUMNS)
                        .from(MENU)
                        .where(MENU.ID.in(menuIds)).orderBy(MENU.SORT.asc(), MENU.CREATE_TIME.desc()),
                MenuVo.class
        );
        return buildMenuTree(result, null);
    }

    private List<Menu> addParentMenu(List<Menu> list) {
        List<Long> parentIds = list.stream().map(Menu::getParentId).filter(Objects::nonNull).collect(Collectors.toList());
        while (CollUtil.isNotEmpty(parentIds)) {
            QueryWrapper qw = QueryWrapper.create()
                    .select(MENU.ALL_COLUMNS)
                    .from(MENU)
                    .where(MENU.ID.in(parentIds));
            List<Menu> parentList = menuService.list(qw);
            list.addAll(parentList);
            parentIds = parentList.stream().map(Menu::getParentId).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<RouteVo> getUserRoutes() {
        List<Long> roleIds = getCurrentUserRoleIds();
        QueryWrapper qw = QueryWrapper.create()
                .select(MENU.ALL_COLUMNS, MENU.MENU_NAME_PATH.as(RouteVo::getBreadCrumb))
                .from(MENU)
                .where(MENU.ID.in(
                        QueryWrapper.create()
                                .select(ROLE_MENU.MENU_ID)
                                .from(ROLE_MENU)
                                .where(ROLE_MENU.ROLE_ID.in(roleIds))
                )).orderBy(MENU.SORT.asc(), MENU.CREATE_TIME.desc());
        return menuService.listAs(qw, RouteVo.class).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void updateSelfInfo(SelfUserDto dto) {
        User user = getById(StpUtil.getLoginIdAsLong());
        Assert.notNull(user);
        BeanUtil.copyProperties(dto, user);
        String hashPassword = PasswordUtils.hashPassword(dto.getNewPassword(), user.getSalt());
        user.setPassword(hashPassword);
        updateById(user);
    }

    public List<MenuVo> buildMenuTree(List<MenuVo> list, String parentId) {
        if (list.isEmpty()) {
            return Lists.newArrayList();
        }
        return list.stream()
                .filter(t -> {
                    if (StrUtil.isBlank(parentId)) {
                        return Objects.isNull(t.getParentId());
                    }
                    return parentId.equals(t.getParentId());
                })
                .peek(t -> {
                    List<MenuVo> children = buildMenuTree(list, t.getId());
                    if (CollUtil.isNotEmpty(children)) {
                        t.setChildren(children);
                    }
                })
                .collect(Collectors.toList());
    }

}
