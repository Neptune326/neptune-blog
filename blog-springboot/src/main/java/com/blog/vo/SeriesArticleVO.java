package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系列文章视图对象
 */
@Data
public class SeriesArticleVO {

    private Long id;

    private String title;

    private String summary;

    private String coverUrl;

    private Integer status;

    private Integer seriesSort;

    private LocalDateTime publishTime;

    private LocalDateTime createTime;
}
