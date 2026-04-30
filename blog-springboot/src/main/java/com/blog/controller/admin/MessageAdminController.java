package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.annotation.OperationLog;
import com.blog.common.result.Result;
import com.blog.entity.Message;
import com.blog.mapper.MessageMapper;
import com.blog.vo.AdminMessageVO;
import com.blog.vo.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * 留言后台管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/messages")
@Validated
@RequiredArgsConstructor
public class MessageAdminController {

    private final MessageMapper messageMapper;

    @GetMapping
    public Result<PageVO<AdminMessageVO>> list(
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize,
            @RequestParam(required = false) Integer status) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .orderByDesc(Message::getCreateTime);
        if (status != null) {
            wrapper.eq(Message::getStatus, status);
        }
        messageMapper.selectPage(page, wrapper);
        PageVO<AdminMessageVO> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords().stream().map(this::toAdminMessageVO).collect(Collectors.toList()));
        return Result.success(vo);
    }

    @OperationLog(module = "留言管理", action = "审核通过")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        Message message = new Message();
        message.setId(id);
        message.setStatus(1);
        messageMapper.updateById(message);
        return Result.success();
    }

    @OperationLog(module = "留言管理", action = "拒绝留言")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        Message message = new Message();
        message.setId(id);
        message.setStatus(2);
        messageMapper.updateById(message);
        return Result.success();
    }

    @OperationLog(module = "留言管理", action = "删除留言")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        messageMapper.deleteById(id);
        return Result.success();
    }

    private AdminMessageVO toAdminMessageVO(Message message) {
        AdminMessageVO adminMessageVO = new AdminMessageVO();
        BeanUtils.copyProperties(message, adminMessageVO);
        return adminMessageVO;
    }
}