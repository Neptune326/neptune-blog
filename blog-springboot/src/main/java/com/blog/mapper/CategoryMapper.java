package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Category;
import com.blog.vo.CategoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分类 Mapper 接口
 * 继承 BaseMapper 获得基础 CRUD 操作，并提供联表统计文章数的自定义查询
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询所有分类及其关联的文章数量
     * 通过 LEFT JOIN 联表统计每个分类下已发布且未删除的文章数
     *
     * @return 包含文章数量的分类视图对象列表
     */
    List<CategoryVO> selectCategoryWithArticleCount();
}
