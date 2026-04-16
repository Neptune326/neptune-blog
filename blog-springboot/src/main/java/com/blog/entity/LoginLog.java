package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 */
@Data
@TableName("login_log")
public class LoginLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 登录用户名 */
    private String username;
    /** 登录 IP */
    private String loginIp;
    /** 登录时间 */
    private LocalDateTime loginTime;
    /** 状态：1=成功，0=失败 */
    private Integer status;
    /** 失败原因 */
    private String failReason;
    /** 浏览器 UA */
    private String userAgent;
}
