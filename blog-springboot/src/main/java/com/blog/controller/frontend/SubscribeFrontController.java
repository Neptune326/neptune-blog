package com.blog.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.common.result.ResultCode;
import com.blog.entity.Subscriber;
import com.blog.mapper.SubscriberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/subscribers")
@Validated
@RequiredArgsConstructor
public class SubscribeFrontController {
    private final SubscriberMapper subscriberMapper;

    @PostMapping
    public Result<Void> subscribe(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return Result.error(ResultCode.BAD_REQUEST, "邮箱格式不正确");
        }
        Subscriber existing = subscriberMapper.selectOne(new LambdaQueryWrapper<Subscriber>()
                .eq(Subscriber::getEmail, email));
        if (existing != null) {
            if (existing.getStatus() != null && existing.getStatus() == 1) {
                return Result.error(ResultCode.BAD_REQUEST, "该邮箱已订阅");
            }
            existing.setStatus(1);
            subscriberMapper.updateById(existing);
            return Result.success();
        }
        Subscriber subscriber = new Subscriber();
        subscriber.setEmail(email.trim());
        subscriber.setStatus(1);
        subscriber.setSource("frontend");
        subscriber.setCreateTime(LocalDateTime.now());
        subscriberMapper.insert(subscriber);
        return Result.success();
    }
}
