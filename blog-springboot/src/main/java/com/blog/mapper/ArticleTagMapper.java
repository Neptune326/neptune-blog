package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章标签关联 Mapper 接口
 * 继承 BaseMapper 获得基础 CRUD 操作
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
}
