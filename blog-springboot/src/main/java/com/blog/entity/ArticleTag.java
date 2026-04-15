package com.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文章标签关联实体类
 * 对应数据库 article_tag 表，不需要审计字段，不继承 BaseEntity
 */
@Data
@TableName("article_tag")
public class ArticleTag {

    /** 文章 ID */
    private Long articleId;

    /** 标签 ID */
    private Long tagId;
}
