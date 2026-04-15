package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表视图对象
 * 用于文章列表展示，不包含正文内容，减少数据传输量
 */
@Data
public class ArticleListVO {

    /** 文章 ID */
    private Long id;

    /** 文章标题 */
    private String title;

    /** 文章摘要 */
    private String summary;

    /** 封面图片 URL */
    private String coverUrl;

    /** 所属分类 ID */
    private Long categoryId;

    /** 所属分类名称 */
    private String categoryName;

    /** 发布状态：0=草稿，1=已发布 */
    private Integer status;

    /** 阅读次数 */
    private Integer viewCount;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 文章标签列表 */
    private List<TagVO> tags;
}
