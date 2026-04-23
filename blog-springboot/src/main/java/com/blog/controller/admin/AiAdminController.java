package com.blog.controller.admin;

import com.blog.common.result.Result;
import com.blog.dto.AiWritingReq;
import com.blog.service.AiService;
import com.blog.vo.AiWritingRsp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI admin API.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/ai")
@Validated
@RequiredArgsConstructor
public class AiAdminController {

    private final AiService aiService;

    @PostMapping("/writing")
    public Result<AiWritingRsp> writing(@Valid @RequestBody AiWritingReq req) {
        log.info("[AI writing] admin request, action: {}", req.getAction());
        return Result.success(aiService.writing(req));
    }
}
