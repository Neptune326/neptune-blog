package com.neptune.vo.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectVo {
    /**
     * 标题
     */
    private String label;
    /**
     * 值
     */
    private String value;

    public static SelectVo create(String label, Object value) {
        return SelectVo.builder().label(label).value(value.toString()).build();
    }

}
