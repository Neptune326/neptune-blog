package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论提交请求 DTO
 */
@Data
public class CommentDTO {

    /** 所属文章 ID，不能为空 */
    @NotNull(message = "文章 ID 不能为空")
    private Long articleId;

    /** 评论者昵称，不能为空 */
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    /** 评论者邮箱（可选） */
    private String email;

    /** 评论内容，不能为空 */
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
