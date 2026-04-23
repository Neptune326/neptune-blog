package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章历史视图对象
 */
@Data
public class ArticleHistoryVO {

    private Long id;

    private Long articleId;

    private String title;

    private Integer version;

    private String remark;

    private LocalDateTime createTime;

    private String createBy;
}
