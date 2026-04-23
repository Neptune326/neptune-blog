package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 留言提交请求 DTO
 */
@Data
public class MessageDTO {

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String email;

    @NotBlank(message = "留言内容不能为空")
    private String content;
}
