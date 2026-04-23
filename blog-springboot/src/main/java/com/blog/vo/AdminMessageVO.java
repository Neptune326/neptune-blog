package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台留言视图对象
 */
@Data
public class AdminMessageVO {

    private Long id;

    private String nickname;

    private String email;

    private String content;

    private Integer status;

    private LocalDateTime createTime;

    private String createBy;
}
