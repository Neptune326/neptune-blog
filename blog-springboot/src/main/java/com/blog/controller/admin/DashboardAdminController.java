package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.entity.Comment;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.MessageMapper;
import com.blog.mapper.TagMapper;
import com.blog.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/api/admin/dashboard")
@Validated
@RequiredArgsConstructor
public class DashboardAdminController {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final CommentMapper commentMapper;
    private final MessageMapper messageMapper;
    private final Executor blogTaskExecutor;

    @GetMapping
    public Result<DashboardVO> dashboard() {
        CompletableFuture<Long> articleCountFuture = CompletableFuture.supplyAsync(() -> articleMapper.selectCount(null), blogTaskExecutor);
        CompletableFuture<Long> categoryCountFuture = CompletableFuture.supplyAsync(() -> categoryMapper.selectCount(null), blogTaskExecutor);
        CompletableFuture<Long> tagCountFuture = CompletableFuture.supplyAsync(() -> tagMapper.selectCount(null), blogTaskExecutor);
        CompletableFuture<Long> commentCountFuture = CompletableFuture.supplyAsync(() -> commentMapper.selectCount(null), blogTaskExecutor);
        CompletableFuture<Long> pendingCommentCountFuture = CompletableFuture.supplyAsync(() -> commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>().eq(Comment::getStatus, 0)
        ), blogTaskExecutor);
        CompletableFuture<Long> messageCountFuture = CompletableFuture.supplyAsync(() -> messageMapper.selectCount(null), blogTaskExecutor);

        CompletableFuture.allOf(
                articleCountFuture,
                categoryCountFuture,
                tagCountFuture,
                commentCountFuture,
                pendingCommentCountFuture,
                messageCountFuture
        ).join();

        DashboardVO vo = new DashboardVO();
        vo.setArticleCount(articleCountFuture.join());
        vo.setCategoryCount(categoryCountFuture.join());
        vo.setTagCount(tagCountFuture.join());
        vo.setCommentCount(commentCountFuture.join());
        vo.setPendingCommentCount(pendingCommentCountFuture.join());
        vo.setMessageCount(messageCountFuture.join());
        return Result.success(vo);
    }
}
