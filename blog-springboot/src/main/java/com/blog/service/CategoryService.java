package com.blog.service;

import com.blog.dto.CategoryDTO;
import com.blog.vo.CategoryVO;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 查询所有分类（含文章数量）
     */
    List<CategoryVO> list();

    /**
     * 创建分类
     */
    void create(CategoryDTO dto);

    /**
     * 更新分类
     */
    void update(Long id, CategoryDTO dto);

    /**
     * 删除分类
     */
    void delete(Long id);
}
