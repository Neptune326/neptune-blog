package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Tag;
import com.blog.vo.TagVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 标签 Mapper 接口
 * 继承 BaseMapper 获得基础 CRUD 操作，并提供联表统计文章数的自定义查询
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 查询所有标签及其关联的文章数量
     * 通过 LEFT JOIN 联表统计每个标签下未删除文章的数量
     *
     * @return 包含文章数量的标签视图对象列表
     */
    List<TagVO> selectTagWithArticleCount();
}
