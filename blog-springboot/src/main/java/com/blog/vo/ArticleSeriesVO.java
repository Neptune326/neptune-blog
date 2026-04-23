package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章系列视图对象
 */
@Data
public class ArticleSeriesVO {

    private Long id;

    private String name;

    private String description;

    private String coverUrl;

    private Integer sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
