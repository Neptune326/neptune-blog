package com.neptune.dto;

import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

@Data
public class TagDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Null(message = "ID必须为空", groups = Create.class)
    @NotNull(message = "ID不能为空", groups = Update.class)
    private Long id;

    @NotBlank(message = "标签不能为空")
    private String name;

    private String sort;

}
