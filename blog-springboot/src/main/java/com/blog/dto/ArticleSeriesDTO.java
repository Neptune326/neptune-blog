package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 文章系列请求 DTO
 */
@Data
public class ArticleSeriesDTO {

    @NotBlank(message = "系列名称不能为空")
    private String name;

    private String description;

    private String coverUrl;

    private Integer sort;
}
