package com.neptune.dto;

import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

@Data
@Builder
public class ArticleDto implements Serializable {

    @Null(message = "主键必须为空", groups = Create.class)
    @NotNull(message = "主键不能为空", groups = Update.class)
    private Long id;

    private Integer status;

    private Long categoryId;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String cover;

    private String summary;

    private String content;

    private String isTop;

    private String isOriginal;

    private String originalUrl;

    private Long[] tagIds;

}
