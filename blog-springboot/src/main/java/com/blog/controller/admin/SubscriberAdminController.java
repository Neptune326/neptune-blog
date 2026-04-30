package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.annotation.OperationLog;
import com.blog.common.result.Result;
import com.blog.entity.Subscriber;
import com.blog.mapper.SubscriberMapper;
import com.blog.vo.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/subscribers")
@Validated
@RequiredArgsConstructor
public class SubscriberAdminController {
    private final SubscriberMapper subscriberMapper;

    @GetMapping
    public Result<PageVO<Subscriber>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "20") Integer pageSize,
                                           @RequestParam(required = false) Integer status) {
        Page<Subscriber> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Subscriber> wrapper = new LambdaQueryWrapper<Subscriber>()
                .orderByDesc(Subscriber::getCreateTime);
        if (status != null) {
            wrapper.eq(Subscriber::getStatus, status);
        }
        subscriberMapper.selectPage(page, wrapper);
        return Result.success(PageVO.of(page));
    }

    @OperationLog(module = "订阅管理", action = "切换订阅状态")
    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        Integer status = body.get("status");
        Subscriber update = new Subscriber();
        update.setId(id);
        update.setStatus(status == null ? 0 : status);
        subscriberMapper.updateById(update);
        return Result.success();
    }

    @OperationLog(module = "订阅管理", action = "删除订阅")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        subscriberMapper.deleteById(id);
        return Result.success();
    }
}
