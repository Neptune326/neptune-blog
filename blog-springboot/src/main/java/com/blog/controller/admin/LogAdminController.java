package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.result.Result;
import com.blog.entity.LoginLog;
import com.blog.entity.OperationLog;
import com.blog.mapper.LoginLogMapper;
import com.blog.mapper.OperationLogMapper;
import com.blog.vo.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 日志查询后台接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class LogAdminController {

    private final OperationLogMapper operationLogMapper;
    private final LoginLogMapper loginLogMapper;

    /**
     * 操作日志列表
     * GET /api/admin/logs/operation
     */
    @GetMapping("/operation")
    public Result<PageVO<OperationLog>> operationLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String operator,
            @RequestParam(required = false) String module) {

        log.debug("查询操作日志，操作人：{}，模块：{}", operator, module);
        Page<OperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .orderByDesc(OperationLog::getCreateTime);
        if (operator != null && !operator.isEmpty()) {
            wrapper.like(OperationLog::getOperator, operator);
        }
        if (module != null && !module.isEmpty()) {
            wrapper.like(OperationLog::getModule, module);
        }
        operationLogMapper.selectPage(page, wrapper);

        PageVO<OperationLog> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords());
        return Result.success(vo);
    }

    /**
     * 登录日志列表
     * GET /api/admin/logs/login
     */
    @GetMapping("/login")
    public Result<PageVO<LoginLog>> loginLogs(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {

        log.debug("查询登录日志，用户名：{}，状态：{}", username, status);
        Page<LoginLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<LoginLog>()
                .orderByDesc(LoginLog::getLoginTime);
        if (username != null && !username.isEmpty()) {
            wrapper.like(LoginLog::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(LoginLog::getStatus, status);
        }
        loginLogMapper.selectPage(page, wrapper);

        PageVO<LoginLog> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords());
        return Result.success(vo);
    }

    /**
     * 清空操作日志
     * DELETE /api/admin/logs/operation
     */
    @DeleteMapping("/operation")
    public Result<Void> clearOperationLogs() {
        operationLogMapper.delete(null);
        log.info("操作日志已清空");
        return Result.success();
    }

    /**
     * 清空登录日志
     * DELETE /api/admin/logs/login
     */
    @DeleteMapping("/login")
    public Result<Void> clearLoginLogs() {
        loginLogMapper.delete(null);
        log.info("登录日志已清空");
        return Result.success();
    }
}
