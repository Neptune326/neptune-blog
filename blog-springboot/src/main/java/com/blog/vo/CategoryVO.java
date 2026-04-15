package com.blog.vo;

import lombok.Data;

/**
 * 分类视图对象
 * 用于返回分类信息及其关联的文章数量
 */
@Data
public class CategoryVO {

    /** 分类 ID */
    private Long id;

    /** 分类名称 */
    private String name;

    /** 分类描述 */
    private String description;

    /** 关联的文章数量 */
    private Long articleCount;
}
