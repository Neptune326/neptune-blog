package com.neptune.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListVo {

    private String id;
    private String createTime;
    private String updateTime;
    private String status;
    private String title;
    private String content;
    private String isTop;
    private String publishTime;

}
