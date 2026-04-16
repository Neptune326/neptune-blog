package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章实体类
 * 对应数据库 article 表，继承 BaseEntity 获得审计字段
 * 使用 @TableLogic 实现软删除，MyBatis-Plus 自动过滤 deleted=1 的记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends BaseEntity {

    /** 主键 ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 文章标题 */
    private String title;

    /** 文章正文（Markdown 格式） */
    private String content;

    /** 文章摘要 */
    private String summary;

    /** 封面图片 URL */
    private String coverUrl;

    /** 所属分类 ID */
    private Long categoryId;

    /** 发布状态：0=草稿，1=已发布 */
    private Integer status;

    /** 是否置顶：0=否，1=是 */
    private Integer isTop;

    /** 阅读次数 */
    private Integer viewCount;

    /** 软删除标志：0=正常，1=已删除，MyBatis-Plus 自动处理 */
    @TableLogic
    private Integer deleted;
}
