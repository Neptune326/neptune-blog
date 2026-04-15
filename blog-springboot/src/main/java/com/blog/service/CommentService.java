package com.blog.service;

import com.blog.dto.CommentDTO;
import com.blog.vo.CommentVO;
import com.blog.vo.PageVO;

/**
 * 评论服务接口
 */
public interface CommentService {

    /**
     * 后台分页查询评论列表，支持按状态筛选
     */
    PageVO<CommentVO> adminList(Integer pageNum, Integer pageSize, Integer status);

    /**
     * 审核通过评论
     */
    void approve(Long id);

    /**
     * 拒绝评论
     */
    void reject(Long id);

    /**
     * 删除评论（物理删除）
     */
    void delete(Long id);

    /**
     * 前台提交评论
     */
    void submit(CommentDTO dto);

    /**
     * 前台按文章 ID 分页查询已通过的评论
     */
    PageVO<CommentVO> listByArticle(Long articleId, Integer pageNum, Integer pageSize);
}
