package com.blog.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计字段抽象基类
 * 所有业务实体继承此类，自动获得创建/更新时间和操作人字段
 * 字段由 BlogMetaObjectHandler 在 INSERT/UPDATE 时自动填充
 */
@Data
public abstract class BaseEntity {

    /** 创建时间，INSERT 时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 创建人，INSERT 时自动填充（管理员用户名或 "visitor"） */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 更新时间，INSERT 和 UPDATE 时自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 更新人，INSERT 和 UPDATE 时自动填充（管理员用户名或 "visitor"） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}
