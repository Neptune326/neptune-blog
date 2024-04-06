package com.neptune.dto;

import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

@Data
public class ResourceDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Null(message = "资源ID必须为空", groups = Create.class)
    @NotNull(message = "资源ID不能为空", groups = Update.class)
    private Long id;


    @NotBlank(message = "资源名称不能为空")
    private String name;

    @NotBlank(message = "资源编码不能为空")
    private String code;

    @NotNull(message = "所属菜单不能为空")
    private Long menuId;

    private Integer sort;


}
