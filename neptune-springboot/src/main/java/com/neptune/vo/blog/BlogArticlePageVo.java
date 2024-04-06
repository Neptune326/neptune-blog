package com.neptune.vo.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogArticlePageVo {

    private String id;
    private String createTime;
    private String title;
    private String cover;

}
