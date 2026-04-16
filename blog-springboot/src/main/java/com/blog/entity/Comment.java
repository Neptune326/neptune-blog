package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论实体类
 * 对应数据库 comment 表，继承 BaseEntity 获得审计字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comment")
public class Comment extends BaseEntity {

    /** 主键 ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 所属文章 ID */
    private Long articleId;

    /** 父评论 ID（null 表示顶级评论） */
    private Long parentId;

    /** 回复的用户昵称 */
    private String replyTo;

    /** 评论者昵称 */
    private String nickname;

    /** 评论者邮箱（可选） */
    private String email;

    /** 评论内容 */
    private String content;

    /** 审核状态：0=待审核，1=已通过，2=已拒绝 */
    private Integer status;
}
