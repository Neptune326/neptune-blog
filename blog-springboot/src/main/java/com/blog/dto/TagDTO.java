package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 标签创建/更新请求 DTO
 */
@Data
public class TagDTO {

    /** 标签名称，不能为空 */
    @NotBlank(message = "标签名称不能为空")
    private String name;
}
