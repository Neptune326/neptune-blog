package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article_history")
public class ArticleHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long articleId;
    private String title;
    private String content;
    private Integer version;
    private String remark;
    private LocalDateTime createTime;
    private String createBy;
}
