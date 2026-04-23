package com.blog.controller.frontend;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.entity.FriendLink;
import com.blog.mapper.FriendLinkMapper;
import com.blog.vo.FriendLinkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 友链前台接口
 */
@RestController
@RequestMapping("/api/friend-links")
@Validated
@RequiredArgsConstructor
public class FriendLinkFrontController {

    private final FriendLinkMapper friendLinkMapper;

    @GetMapping
    public Result<List<FriendLinkVO>> list() {
        List<FriendLink> list = friendLinkMapper.selectList(
                new LambdaQueryWrapper<FriendLink>().orderByAsc(FriendLink::getSort)
        );
        return Result.success(list.stream().map(this::toFriendLinkVO).collect(Collectors.toList()));
    }

    private FriendLinkVO toFriendLinkVO(FriendLink friendLink) {
        FriendLinkVO friendLinkVO = new FriendLinkVO();
        BeanUtils.copyProperties(friendLink, friendLinkVO);
        return friendLinkVO;
    }
}