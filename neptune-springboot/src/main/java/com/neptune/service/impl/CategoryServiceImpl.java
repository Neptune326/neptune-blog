package com.neptune.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.neptune.entity.Category;
import com.neptune.mapper.CategoryMapper;
import com.neptune.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * 菜单表 服务层实现。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
