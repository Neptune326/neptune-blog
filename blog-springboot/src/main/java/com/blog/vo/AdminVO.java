package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员视图对象
 */
@Data
public class AdminVO {

    private Long id;

    private String username;

    private String role;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
