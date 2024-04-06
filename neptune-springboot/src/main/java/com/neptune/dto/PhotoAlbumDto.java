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
public class PhotoAlbumDto implements Serializable {

    @Null(message = "主键必须为空", groups = Create.class)
    @NotNull(message = "主键不能为空", groups = Update.class)
    private Long id;

    @NotBlank(message = "相册名称不能为空")
    private String name;

    private String info;

    private String cover;

}
