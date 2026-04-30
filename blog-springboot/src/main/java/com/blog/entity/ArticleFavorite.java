package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article_favorite")
public class ArticleFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private String visitorId;
    private LocalDateTime createTime;
}
