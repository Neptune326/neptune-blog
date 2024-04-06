package com.neptune.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.neptune.annotation.BackLog;
import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import com.neptune.constant.MinioConstant;
import com.neptune.dto.SelfUserDto;
import com.neptune.dto.UserDto;
import com.neptune.entity.ResponseResult;
import com.neptune.entity.User;
import com.neptune.service.UserRoleService;
import com.neptune.service.UserService;
import com.neptune.strategy.UploadStrategy;
import com.neptune.utils.CommonUtils;
import com.neptune.utils.PasswordUtils;
import com.neptune.vo.admin.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

import static com.neptune.entity.table.UserRoleTableDef.USER_ROLE;
import static com.neptune.entity.table.UserTableDef.USER;

@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UploadStrategy uploadStrategy;

    @SaCheckLogin
    @BackLog(module = "用户", type = "新增")
    @PostMapping("save")
    public ResponseResult<?> save(@RequestBody @Validated(Create.class) UserDto dto) {
        User t = userService.saveUpdate(dto);
        return ResponseResult.success("保存成功", t.getId());
    }

    @SaCheckLogin
    @BackLog(module = "用户", type = "修改")
    @PostMapping("update")
    public ResponseResult<?> update(@RequestBody @Validated(Update.class) UserDto dto) {
        User t = userService.saveUpdate(dto);
        return ResponseResult.success("保存成功", t.getId());
    }

    @SaCheckLogin
    @BackLog(module = "用户", type = "修改个人信息")
    @PostMapping("updateSelfInfo")
    public ResponseResult<?> updateSelfInfo(@RequestBody @Validated(Update.class) SelfUserDto dto) {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (!PasswordUtils.verify(dto.getOldPassword(), user.getSalt(), user.getPassword())) {
            return ResponseResult.fail("原密码错误");
        }
        userService.updateSelfInfo(dto);
        return ResponseResult.success("保存成功");
    }

    @SaCheckLogin
    @BackLog(module = "用户", type = "删除")
    @GetMapping("remove")
    public ResponseResult<?> remove(@RequestParam Long id) {
        boolean flag = userService.removeById(id);
        return ResponseResult.result(flag);
    }

    @SaCheckLogin
    @GetMapping("editInfo")
    public ResponseResult<UserVo> editInfo(@RequestParam Long id) {
        User t = userService.getById(id);
        UserVo vo = BeanUtil.copyProperties(t, UserVo.class);
        String[] roleIds = userRoleService.list(QueryWrapper.create().where(USER_ROLE.USER_ID.eq(t.getId())))
                .stream().map(u -> u.getRoleId().toString()).toArray(String[]::new);
        vo.setRoleIds(roleIds);
        return ResponseResult.success(vo);
    }

    @SaCheckLogin
    @GetMapping("info")
    public ResponseResult<UserVo> info(@RequestParam Long id) {
        User t = userService.getById(id);
        UserVo vo = BeanUtil.copyProperties(t, UserVo.class);
        return ResponseResult.success(vo);
    }

    @SaCheckLogin
    @GetMapping("page")
    public ResponseResult<Page> page(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request
    ) {
        Map<String, String> search = CommonUtils.createSearch(request, "userName", "nickName");
        Page<UserVo> page = userService.getPage(pageNumber, pageSize, search);
        return ResponseResult.success(page);
    }


    @SaCheckLogin
    @GetMapping("check")
    public ResponseResult<?> check(Long id, String userName, String nickName) {
        QueryWrapper qw = QueryChain.create().where(USER.ID.ne(id, Objects.nonNull(id)));
        if (StrUtil.isNotBlank(userName)) {
            long count = userService.count(qw.and(USER.USER_NAME.eq(userName)));
            return ResponseResult.result(count == 0, "用户名已存在");
        }
        if (StrUtil.isNotBlank(nickName)) {
            long count = userService.count(qw.and(USER.NICK_NAME.eq(nickName)));
            return ResponseResult.result(count == 0, "昵称已存在");
        }
        return ResponseResult.success();
    }


    @SaCheckLogin
    @BackLog(module = "用户", type = "上传头像")
    @PostMapping("/uploadAvatar")
    public ResponseResult<String> uploadAvatar(MultipartFile file) {
        String url = uploadStrategy.uploadAndGetUrl(file, MinioConstant.BUCKET_DEFAULT);
        return ResponseResult.success("上传成功", url);
    }

}
