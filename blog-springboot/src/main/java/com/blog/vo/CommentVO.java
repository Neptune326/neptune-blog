package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论视图对象
 * 用于返回评论信息
 */
@Data
public class CommentVO {

    /** 评论 ID */
    private Long id;

    /** 所属文章 ID */
    private Long articleId;

    /** 评论者昵称 */
    private String nickname;

    /** 评论内容 */
    private String content;

    /** 审核状态：0=待审核，1=已通过，2=已拒绝 */
    private Integer status;

    /** 评论时间 */
    private LocalDateTime createTime;
}
