package com.neptune.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuVo {

    private String id;
    private String createTime;
    private String name;
    private String code;
    private String type;
    private String route;
    private String componentName;
    private String componentPath;
    private String icon;
    private String sort;
    private String parentId;
    private String parentName;
    private String level;
    private String isHidden;
    private List<MenuVo> children;

}
