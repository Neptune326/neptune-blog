package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 前台留言视图对象
 */
@Data
public class MessageVO {

    private Long id;

    private String nickname;

    private String content;

    private Integer status;

    private LocalDateTime createTime;
}
