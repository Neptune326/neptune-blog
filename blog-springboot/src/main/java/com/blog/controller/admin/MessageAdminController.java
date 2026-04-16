package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.annotation.OperationLog;
import com.blog.common.result.Result;
import com.blog.entity.Message;
import com.blog.mapper.MessageMapper;
import com.blog.vo.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 留言板后台管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
public class MessageAdminController {

    private final MessageMapper messageMapper;

    @GetMapping
    public Result<PageVO<Message>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<Message>()
                .orderByDesc(Message::getCreateTime);
        if (status != null) wrapper.eq(Message::getStatus, status);
        messageMapper.selectPage(page, wrapper);
        PageVO<Message> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords());
        return Result.success(vo);
    }

    @OperationLog(module = "留言管理", action = "审核通过")
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        Message m = new Message(); m.setId(id); m.setStatus(1);
        messageMapper.updateById(m);
        return Result.success();
    }

    @OperationLog(module = "留言管理", action = "拒绝留言")
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        Message m = new Message(); m.setId(id); m.setStatus(2);
        messageMapper.updateById(m);
        return Result.success();
    }

    @OperationLog(module = "留言管理", action = "删除留言")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        messageMapper.deleteById(id);
        return Result.success();
    }
}
