package com.neptune.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleEditVo {

    private String id;
    private String name;
    private String code;
    private String sort;

    private String[] menuIds;
    private String[] resourceIds;

}
