package com.neptune.dto;

import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

@Data
public class RoleDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Null(message = "角色ID必须为空", groups = Create.class)
    @NotNull(message = "角色ID不能为空", groups = Update.class)
    private Long id;

    @NotBlank(message = "角色名称不能为空")
    private String name;

    @NotBlank(message = "角色编码不能为空")
    private String code;

    private Integer sort;


}
