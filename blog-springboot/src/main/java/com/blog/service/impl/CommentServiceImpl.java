package com.blog.service.impl;

import com.blog.common.util.XssUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.dto.CommentDTO;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CommentMapper;
import com.blog.service.CommentService;
import com.blog.vo.CommentVO;
import com.blog.vo.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类（支持嵌套回复）
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;

    @Override
    public PageVO<CommentVO> adminList(Integer pageNum, Integer pageSize, Integer status) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getCreateTime);
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        commentMapper.selectPage(page, wrapper);

        List<CommentVO> voList = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        PageVO<CommentVO> result = new PageVO<>();
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setList(voList);
        return result;
    }

    @Override
    public void approve(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(1);
        commentMapper.updateById(comment);
    }

    @Override
    public void reject(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(2);
        commentMapper.updateById(comment);
    }

    @Override
    public void delete(Long id) {
        // 同时删除该评论的所有子评论
        commentMapper.delete(new LambdaQueryWrapper<Comment>().eq(Comment::getParentId, id));
        commentMapper.deleteById(id);
    }

    @Override
    public void submit(CommentDTO dto) {
        Article article = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getId, dto.getArticleId())
                        .eq(Article::getStatus, 1)
        );
        if (article == null) {
            throw new BusinessException(ResultCode.COMMENT_ARTICLE_NOT_PUBLISHED);
        }

        Comment comment = new Comment();
        comment.setArticleId(dto.getArticleId());
        comment.setParentId(dto.getParentId());
        comment.setReplyTo(dto.getReplyTo() != null ? XssUtils.sanitize(dto.getReplyTo()) : null);
        comment.setNickname(XssUtils.sanitize(dto.getNickname()));
        comment.setEmail(dto.getEmail());
        comment.setContent(XssUtils.sanitize(dto.getContent()));
        comment.setStatus(0);
        commentMapper.insert(comment);
    }

    @Override
    public PageVO<CommentVO> listByArticle(Long articleId, Integer pageNum, Integer pageSize) {
        // 查询该文章所有已通过的顶级评论（分页）
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, 1)
                .isNull(Comment::getParentId)
                .orderByAsc(Comment::getCreateTime);
        commentMapper.selectPage(page, wrapper);

        List<CommentVO> topList = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        if (!topList.isEmpty()) {
            // 查询所有子评论（不分页，一次性加载）
            List<Long> topIds = topList.stream().map(CommentVO::getId).collect(Collectors.toList());
            List<Comment> children = commentMapper.selectList(
                    new LambdaQueryWrapper<Comment>()
                            .eq(Comment::getArticleId, articleId)
                            .eq(Comment::getStatus, 1)
                            .in(Comment::getParentId, topIds)
                            .orderByAsc(Comment::getCreateTime)
            );

            // 按 parentId 分组
            Map<Long, List<CommentVO>> childMap = children.stream()
                    .map(this::toVO)
                    .collect(Collectors.groupingBy(CommentVO::getParentId));

            // 填充子评论
            for (CommentVO top : topList) {
                top.setChildren(childMap.getOrDefault(top.getId(), new ArrayList<>()));
            }
        }

        PageVO<CommentVO> result = new PageVO<>();
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setList(topList);
        return result;
    }

    private CommentVO toVO(Comment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        return vo;
    }
}
