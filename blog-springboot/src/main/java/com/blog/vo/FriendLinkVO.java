package com.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 友链视图对象
 */
@Data
public class FriendLinkVO {

    private Long id;

    private String name;

    private String url;

    private String description;

    private String iconUrl;

    private Integer sort;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
