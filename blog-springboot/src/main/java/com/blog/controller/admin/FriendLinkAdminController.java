package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.dto.FriendLinkDTO;
import com.blog.entity.FriendLink;
import com.blog.mapper.FriendLinkMapper;
import com.blog.vo.FriendLinkVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 友链后台管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/friend-links")
@Validated
@RequiredArgsConstructor
public class FriendLinkAdminController {

    private final FriendLinkMapper friendLinkMapper;

    @GetMapping
    public Result<List<FriendLinkVO>> list() {
        List<FriendLink> list = friendLinkMapper.selectList(
                new LambdaQueryWrapper<FriendLink>().orderByAsc(FriendLink::getSort)
        );
        return Result.success(list.stream().map(this::toFriendLinkVO).collect(Collectors.toList()));
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid FriendLinkDTO friendLinkDTO) {
        FriendLink friendLink = new FriendLink();
        BeanUtils.copyProperties(friendLinkDTO, friendLink);
        friendLink.setCreateTime(LocalDateTime.now());
        friendLink.setUpdateTime(LocalDateTime.now());
        if (friendLink.getSort() == null) {
            friendLink.setSort(0);
        }
        friendLinkMapper.insert(friendLink);
        log.info("新增友链: {}", friendLink.getName());
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Valid FriendLinkDTO friendLinkDTO) {
        FriendLink friendLink = new FriendLink();
        BeanUtils.copyProperties(friendLinkDTO, friendLink);
        friendLink.setId(id);
        friendLink.setUpdateTime(LocalDateTime.now());
        friendLinkMapper.updateById(friendLink);
        log.info("更新友链: id={}", id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        friendLinkMapper.deleteById(id);
        log.info("删除友链: id={}", id);
        return Result.success();
    }

    private FriendLinkVO toFriendLinkVO(FriendLink friendLink) {
        FriendLinkVO friendLinkVO = new FriendLinkVO();
        BeanUtils.copyProperties(friendLink, friendLinkVO);
        return friendLinkVO;
    }
}