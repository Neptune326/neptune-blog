package com.neptune.dto;

import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
public class MenuDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Null(message = "菜单ID必须为空", groups = Create.class)
    @NotNull(message = "菜单ID不能为空", groups = Update.class)
    private Long id;

    @NotBlank(message = "菜单名称不能为空")
    private String name;

    @NotBlank(message = "菜单编码不能为空")
    private String code;

    @Max(value = 1, message = "类型只能为0或1")
    @Min(value = 0, message = "类型只能为0或1")
    private Integer type;

    private String route;

    private String componentName;

    private String componentPath;

    private String icon;

    private Integer sort;

    private Long parentId;

    @Max(value = 1, message = "只能为0或1")
    @Min(value = 0, message = "只能为0或1")
    private Integer isHidden;

}
