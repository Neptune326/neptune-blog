package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分类创建/更新请求 DTO
 */
@Data
public class CategoryDTO {

    /** 分类名称，不能为空 */
    @NotBlank(message = "分类名称不能为空")
    private String name;

    /** 分类描述（可选） */
    private String description;
}
