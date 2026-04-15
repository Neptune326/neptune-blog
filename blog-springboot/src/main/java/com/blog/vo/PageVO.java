package com.blog.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 分页响应对象
 * 统一封装分页查询结果，包含总记录数、总页数和当前页数据列表
 *
 * @param <T> 列表元素类型
 */
@Data
public class PageVO<T> {

    /** 总记录数 */
    private Long total;

    /** 总页数 */
    private Long pages;

    /** 当前页数据列表 */
    private List<T> list;

    /**
     * 从 MyBatis-Plus IPage 构建分页响应对象
     *
     * @param page MyBatis-Plus 分页查询结果
     * @param <T>  列表元素类型
     * @return 分页响应对象
     */
    public static <T> PageVO<T> of(IPage<T> page) {
        PageVO<T> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords());
        return vo;
    }
}
