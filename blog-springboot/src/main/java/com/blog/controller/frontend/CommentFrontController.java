package com.blog.controller.frontend;

import com.blog.common.result.Result;
import com.blog.dto.CommentDTO;
import com.blog.service.CommentService;
import com.blog.vo.CommentVO;
import com.blog.vo.PageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 前台评论 Controller
 */
@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class CommentFrontController {

    private final CommentService commentService;

    /**
     * 提交评论
     */
    @PostMapping("/comments")
    public Result<Void> submit(@Valid @RequestBody CommentDTO dto) {
        commentService.submit(dto);
        return Result.success();
    }

    /**
     * 查询指定文章的已通过评论列表
     */
    @GetMapping("/articles/{id}/comments")
    public Result<PageVO<CommentVO>> listByArticle(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(commentService.listByArticle(id, pageNum, pageSize));
    }
}
