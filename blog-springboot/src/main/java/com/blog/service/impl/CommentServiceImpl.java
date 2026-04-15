package com.blog.service.impl;

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

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;

    /**
     * 后台分页查询评论列表，支持按状态筛选
     */
    @Override
    public PageVO<CommentVO> adminList(Integer pageNum, Integer pageSize, Integer status) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getCreateTime);
        // status 非 null 时按状态筛选
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        commentMapper.selectPage(page, wrapper);

        // 转换为 CommentVO
        List<CommentVO> voList = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        PageVO<CommentVO> result = new PageVO<>();
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setList(voList);
        return result;
    }

    /**
     * 审核通过评论（status=1）
     */
    @Override
    public void approve(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(1);
        commentMapper.updateById(comment);
    }

    /**
     * 拒绝评论（status=2）
     */
    @Override
    public void reject(Long id) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setStatus(2);
        commentMapper.updateById(comment);
    }

    /**
     * 物理删除评论
     */
    @Override
    public void delete(Long id) {
        commentMapper.deleteById(id);
    }

    /**
     * 前台提交评论，检查文章是否存在且已发布
     */
    @Override
    public void submit(CommentDTO dto) {
        // 查询文章是否存在且已发布
        Article article = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getId, dto.getArticleId())
                        .eq(Article::getStatus, 1)
        );
        if (article == null) {
            throw new BusinessException(ResultCode.COMMENT_ARTICLE_NOT_PUBLISHED);
        }

        // 构建评论实体，初始状态为待审核
        Comment comment = new Comment();
        comment.setArticleId(dto.getArticleId());
        comment.setNickname(dto.getNickname());
        comment.setEmail(dto.getEmail());
        comment.setContent(dto.getContent());
        comment.setStatus(0);
        commentMapper.insert(comment);
    }

    /**
     * 前台按文章 ID 分页查询已通过的评论，按时间升序
     */
    @Override
    public PageVO<CommentVO> listByArticle(Long articleId, Integer pageNum, Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, 1)
                .orderByAsc(Comment::getCreateTime);
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

    /**
     * Comment 实体转 CommentVO
     */
    private CommentVO toVO(Comment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        return vo;
    }
}
