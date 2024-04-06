package com.neptune.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteVo {

    private String route;
    private String name;
    private String breadCrumb;
    private String componentName;
    private String componentPath;

}
