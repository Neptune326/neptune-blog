package com.blog.controller.frontend;

import com.blog.common.result.Result;
import com.blog.common.result.ResultCode;
import com.blog.service.MetingService;
import com.blog.vo.MetingSongVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台：Meting 歌曲解析（无需登录），供音乐播放器通过 server+id 解析播放地址
 */
@RestController
@RequestMapping("/api/meting")
@RequiredArgsConstructor
public class MetingController {

    private final MetingService metingService;

    @GetMapping("/resolve")
    public Result<MetingSongVO> resolve(
            @RequestParam String server,
            @RequestParam String id) {
        try {
            return Result.success(metingService.resolveSong(server, id));
        } catch (IllegalArgumentException e) {
            return Result.error(ResultCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return Result.error(ResultCode.INTERNAL_ERROR, e.getMessage() != null ? e.getMessage() : "解析失败");
        }
    }
}
