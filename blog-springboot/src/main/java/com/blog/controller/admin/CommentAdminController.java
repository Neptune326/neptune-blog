package com.blog.controller.admin;

import com.blog.common.result.Result;
import com.blog.service.CommentService;
import com.blog.vo.CommentVO;
import com.blog.vo.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 后台评论管理 Controller
 */
@RestController
@RequestMapping("/api/admin/comments")
@Validated
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentService commentService;

    /**
     * 分页查询评论列表（支持按状态筛选）
     */
    @GetMapping
    public Result<PageVO<CommentVO>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(commentService.adminList(pageNum, pageSize, status));
    }

    /**
     * 审核通过评论
     */
    @PutMapping("/{id}/approve")
    public Result<Void> approve(@PathVariable Long id) {
        commentService.approve(id);
        return Result.success();
    }

    /**
     * 拒绝评论
     */
    @PutMapping("/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        commentService.reject(id);
        return Result.success();
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return Result.success();
    }
}
