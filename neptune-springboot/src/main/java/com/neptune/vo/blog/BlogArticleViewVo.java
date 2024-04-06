package com.neptune.vo.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogArticleViewVo {

    private String id;
    private String createTime;
    private String categoryId;
    private String categoryName;
    private String title;
    private String cover;
    private String summary;
    private String content;
    private String isTop;
    private String isOriginal;
    private String originalUrl;
    private String[] tagIds;
    private String[] tagNames;
    private List<BlogArticleAnchorVo> anchors;

}
