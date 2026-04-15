package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 管理员实体类
 * 对应数据库 admin 表，审计字段由初始化脚本直接写入，不继承 BaseEntity
 */
@Data
@TableName("admin")
public class Admin {

    /** 主键 ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码（BCrypt 加密存储） */
    private String password;
}
