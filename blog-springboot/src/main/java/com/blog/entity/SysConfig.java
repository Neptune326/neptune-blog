package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体
 */
@Data
@TableName("sys_config")
public class SysConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 配置键 */
    private String configKey;
    /** 配置值 */
    private String configValue;
    /** 配置说明 */
    private String configDesc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
