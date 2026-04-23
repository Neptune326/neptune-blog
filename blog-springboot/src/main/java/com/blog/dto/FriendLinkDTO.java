package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 友链请求 DTO
 */
@Data
public class FriendLinkDTO {

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "链接地址不能为空")
    private String url;

    private String description;

    private String iconUrl;

    private Integer sort;
}
