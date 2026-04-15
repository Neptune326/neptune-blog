package com.blog.vo;

import lombok.Data;

import java.util.List;

/**
 * 归档视图对象
 * 用于按年月分组展示文章归档数据
 */
@Data
public class ArchiveVO {

    /** 年月，格式为 yyyy-MM，例如 2024-01 */
    private String yearMonth;

    /** 该月文章数量 */
    private Long count;

    /** 该月文章列表 */
    private List<ArticleListVO> articles;
}
