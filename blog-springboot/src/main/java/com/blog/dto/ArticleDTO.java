package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 文章创建/更新请求 DTO
 */
@Data
public class ArticleDTO {

    /** 文章标题，不能为空 */
    @NotBlank(message = "文章标题不能为空")
    private String title;

    /** 文章正文（Markdown 格式），不能为空 */
    @NotBlank(message = "文章正文不能为空")
    private String content;

    /** 文章摘要（可选） */
    private String summary;

    /** 封面图片 URL（可选） */
    private String coverUrl;

    /** 所属分类 ID（可选） */
    private Long categoryId;

    /** 关联标签 ID 列表（可选） */
    private List<Long> tagIds;

    /** 发布状态：0=草稿，1=已发布，默认为草稿 */
    private Integer status = 0;
}
