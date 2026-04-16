package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论视图对象（支持嵌套回复）
 */
@Data
public class CommentVO {
    private Long id;
    private Long articleId;
    private Long parentId;
    private String replyTo;
    private String nickname;
    private String content;
    private Integer status;
    private LocalDateTime createTime;
    /** 子评论（回复列表） */
    private List<CommentVO> children;
}
