package com.neptune.vo.blog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogArticleAnchorVo {

    private Integer index;
    private String text;
    private String level;

}
