package com.blog.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.result.Result;
import com.blog.common.util.XssUtils;
import com.blog.dto.MessageDTO;
import com.blog.entity.Message;
import com.blog.mapper.MessageMapper;
import com.blog.vo.MessageVO;
import com.blog.vo.PageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 留言板前台接口
 */
@Slf4j
@RestController
@RequestMapping("/api/messages")
@Validated
@RequiredArgsConstructor
public class MessageFrontController {

    private final MessageMapper messageMapper;

    @GetMapping
    public Result<PageVO<MessageVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        messageMapper.selectPage(page, new LambdaQueryWrapper<Message>()
                .eq(Message::getStatus, 1)
                .orderByDesc(Message::getCreateTime));
        PageVO<MessageVO> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords().stream().map(this::toMessageVO).collect(Collectors.toList()));
        return Result.success(vo);
    }

    @PostMapping
    public Result<Void> submit(@RequestBody @Valid MessageDTO req) {
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

    private MessageVO toMessageVO(Message message) {
        MessageVO messageVO = new MessageVO();
        BeanUtils.copyProperties(message, messageVO);
        return messageVO;
    }
}