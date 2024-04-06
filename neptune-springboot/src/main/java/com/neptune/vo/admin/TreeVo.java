package com.neptune.vo.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeVo {

    private String label;

    private String value;

    private String icon;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeVo> children;

}
