package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签实体类
 * 对应数据库 tag 表，继承 BaseEntity 获得审计字段
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tag")
public class Tag extends BaseEntity {

    /** 主键 ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标签名称 */
    private String name;
}
