package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.dto.CategoryDTO;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.service.CategoryService;
import com.blog.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    /**
     * 查询所有分类及文章数量
     */
    @Override
    public List<CategoryVO> list() {
        return categoryMapper.selectCategoryWithArticleCount();
    }

    /**
     * 创建分类，检查名称重复
     */
    @Override
    public void create(CategoryDTO dto) {
        // 检查同名分类是否已存在
        long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>().eq(Category::getName, dto.getName())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.CATEGORY_NAME_DUPLICATE);
        }
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        categoryMapper.insert(category);
    }

    /**
     * 更新分类，检查名称重复（排除自身）
     */
    @Override
    public void update(Long id, CategoryDTO dto) {
        // 检查同名分类是否已存在（排除自身）
        long count = categoryMapper.selectCount(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getName, dto.getName())
                        .ne(Category::getId, id)
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.CATEGORY_NAME_DUPLICATE);
        }
        Category category = new Category();
        category.setId(id);
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        categoryMapper.updateById(category);
    }

    /**
     * 删除分类，若该分类下有文章则拒绝删除
     */
    @Override
    public void delete(Long id) {
        // 查询该分类下是否存在未删除的文章
        long articleCount = articleMapper.selectCount(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getCategoryId, id)
                        .eq(Article::getDeleted, 0)
        );
        if (articleCount > 0) {
            throw new BusinessException(ResultCode.CATEGORY_HAS_ARTICLES);
        }
        categoryMapper.deleteById(id);
    }
}
