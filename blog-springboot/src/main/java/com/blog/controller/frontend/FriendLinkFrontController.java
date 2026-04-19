package com.blog.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.entity.FriendLink;
import com.blog.mapper.FriendLinkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 友情链接前台接口（无需鉴权）
 */
@RestController
@RequestMapping("/api/friend-links")
@RequiredArgsConstructor
public class FriendLinkFrontController {

    private final FriendLinkMapper friendLinkMapper;

    @GetMapping
    public Result<List<FriendLink>> list() {
        List<FriendLink> list = friendLinkMapper.selectList(
                new LambdaQueryWrapper<FriendLink>().orderByAsc(FriendLink::getSort)
        );
        return Result.success(list);
    }
}
