package com.blog.vo;

import lombok.Data;

/**
 * 标签视图对象
 * 用于返回标签信息及其关联的文章数量
 */
@Data
public class TagVO {

    /** 标签 ID */
    private Long id;

    /** 标签名称 */
    private String name;

    /** 关联的文章数量 */
    private Long articleCount;
}
