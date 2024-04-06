package com.neptune.dto;

import com.neptune.annotation.Create;
import com.neptune.annotation.Update;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Null(message = "用户ID必须为空", groups = Create.class)
    @NotNull(message = "用户ID不能为空", groups = Update.class)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String userName;

    @NotBlank(message = "昵称不能为空")
    private String nickName;

    @NotBlank(message = "密码不能为空", groups = Create.class)
    @Null(message = "密码必须为空", groups = Update.class)
    private String password;

    private String avatar;

    private Long[] roleIds;

}
