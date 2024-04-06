package com.neptune.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuEditVo {

    private String id;
    private String type;
    private String name;
    private String code;
    private String route;
    private String componentName;
    private String componentPath;
    private String icon;
    private String sort;
    private String parentId;
    private String isHidden;

}
