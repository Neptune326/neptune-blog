package com.blog.property;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.exception.BusinessException;
import com.blog.dto.CommentDTO;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CommentMapper;
import com.blog.service.impl.CommentServiceImpl;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * 评论相关属性测试
 * Feature: personal-blog-system
 */
class CommentPropertyTest {

    // Feature: personal-blog-system, Property 3: 评论初始状态为待审核
    @Property(tries = 100)
    void 评论提交后初始状态为待审核(
            @ForAll("validNicknames") String nickname,
            @ForAll("validContents") String content) {

        CommentMapper commentMapper = Mockito.mock(CommentMapper.class);
        ArticleMapper articleMapper = Mockito.mock(ArticleMapper.class);
        CommentServiceImpl commentService = new CommentServiceImpl(commentMapper, articleMapper);

        // mock：文章存在且已发布
        Article article = new Article();
        article.setId(1L);
        article.setStatus(1);
        Mockito.when(articleMapper.selectOne(Mockito.any(LambdaQueryWrapper.class))).thenReturn(article);

        // 提交评论
        CommentDTO dto = new CommentDTO();
        dto.setArticleId(1L);
        dto.setNickname(nickname);
        dto.setContent(content);
        commentService.submit(dto);

        // 捕获插入的 Comment 对象，断言 status=0（待审核）
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentMapper).insert(captor.capture());
        Assertions.assertEquals(0, captor.getValue().getStatus(),
                "评论提交后初始状态应为 0（待审核）");
    }

    // Feature: personal-blog-system, Property 4: 评论审核状态流转合法性
    @Property(tries = 100)
    void 评论审核状态流转合法(@ForAll("positiveIds") Long commentId) {
        CommentMapper commentMapper = Mockito.mock(CommentMapper.class);
        ArticleMapper articleMapper = Mockito.mock(ArticleMapper.class);
        CommentServiceImpl commentService = new CommentServiceImpl(commentMapper, articleMapper);

        // 测试审核通过：status 应变为 1
        commentService.approve(commentId);
        ArgumentCaptor<Comment> approveCaptor = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentMapper, Mockito.atLeastOnce()).updateById(approveCaptor.capture());
        Comment approvedComment = approveCaptor.getAllValues().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst().orElse(null);
        Assertions.assertNotNull(approvedComment);
        Assertions.assertEquals(1, approvedComment.getStatus(), "审核通过后 status 应为 1");

        // 重置 mock
        Mockito.reset(commentMapper);

        // 测试拒绝：status 应变为 2
        commentService.reject(commentId);
        ArgumentCaptor<Comment> rejectCaptor = ArgumentCaptor.forClass(Comment.class);
        Mockito.verify(commentMapper, Mockito.atLeastOnce()).updateById(rejectCaptor.capture());
        Comment rejectedComment = rejectCaptor.getAllValues().stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst().orElse(null);
        Assertions.assertNotNull(rejectedComment);
        Assertions.assertEquals(2, rejectedComment.getStatus(), "拒绝后 status 应为 2");
    }

    @Provide
    Arbitrary<String> validNicknames() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20);
    }

    @Provide
    Arbitrary<String> validContents() {
        return Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(200);
    }

    @Provide
    Arbitrary<Long> positiveIds() {
        return Arbitraries.longs().between(1L, 100000L);
    }
}
