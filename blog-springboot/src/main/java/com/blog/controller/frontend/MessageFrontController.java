package com.blog.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.result.Result;
import com.blog.common.util.XssUtils;
import com.blog.entity.Message;
import com.blog.mapper.MessageMapper;
import com.blog.vo.PageVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 留言板前台接口
 */
@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageFrontController {

    private final MessageMapper messageMapper;

    /** 获取已通过的留言列表 */
    @GetMapping
    public Result<PageVO<Message>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        messageMapper.selectPage(page, new LambdaQueryWrapper<Message>()
                .eq(Message::getStatus, 1)
                .orderByDesc(Message::getCreateTime));
        PageVO<Message> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords());
        return Result.success(vo);
    }

    /** 提交留言 */
    @PostMapping
    public Result<Void> submit(@RequestBody @Valid MessageRequest req) {
        Message message = new Message();
        message.setNickname(XssUtils.sanitize(req.getNickname()));
        message.setEmail(req.getEmail());
        message.setContent(XssUtils.sanitize(req.getContent()));
        message.setStatus(0);
        message.setCreateTime(LocalDateTime.now());
        message.setCreateBy("visitor");
        messageMapper.insert(message);
        log.info("收到新留言，昵称：{}", req.getNickname());
        return Result.success();
    }

    @Data
    public static class MessageRequest {
        @NotBlank(message = "昵称不能为空")
        private String nickname;
        private String email;
        @NotBlank(message = "留言内容不能为空")
        private String content;
    }
}
