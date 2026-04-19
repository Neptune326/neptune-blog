package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.entity.FriendLink;
import com.blog.mapper.FriendLinkMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 友情链接后台管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/friend-links")
@RequiredArgsConstructor
public class FriendLinkAdminController {

    private final FriendLinkMapper friendLinkMapper;

    /** 获取所有友情链接 */
    @GetMapping
    public Result<List<FriendLink>> list() {
        List<FriendLink> list = friendLinkMapper.selectList(
                new LambdaQueryWrapper<FriendLink>().orderByAsc(FriendLink::getSort)
        );
        return Result.success(list);
    }

    /** 新增友情链接 */
    @PostMapping
    public Result<Void> create(@RequestBody FriendLink friendLink) {
        friendLink.setCreateTime(LocalDateTime.now());
        friendLink.setUpdateTime(LocalDateTime.now());
        if (friendLink.getSort() == null) friendLink.setSort(0);
        friendLinkMapper.insert(friendLink);
        log.info("新增友情链接: {}", friendLink.getName());
        return Result.success();
    }

    /** 更新友情链接 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody FriendLink friendLink) {
        friendLink.setId(id);
        friendLink.setUpdateTime(LocalDateTime.now());
        friendLinkMapper.updateById(friendLink);
        log.info("更新友情链接: id={}", id);
        return Result.success();
    }

    /** 删除友情链接 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        friendLinkMapper.deleteById(id);
        log.info("删除友情链接: id={}", id);
        return Result.success();
    }
}
