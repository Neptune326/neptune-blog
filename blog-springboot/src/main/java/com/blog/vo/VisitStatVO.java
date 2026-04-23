package com.blog.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 访问统计视图对象
 */
@Data
public class VisitStatVO {

    private LocalDate visitDate;

    private Integer pv;

    private Integer uv;
}
