package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 友情链接实体
 */
@Data
@TableName("friend_link")
public class FriendLink {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 链接名称 */
    private String name;

    /** 链接地址 */
    private String url;

    /** 链接描述 */
    private String description;

    /** 图标 URL */
    private String iconUrl;

    /** 排序（越小越靠前） */
    private Integer sort;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
