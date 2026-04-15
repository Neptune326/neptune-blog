package com.blog.service;

import com.blog.dto.TagDTO;
import com.blog.vo.TagVO;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {

    /**
     * 查询所有标签（含文章数量）
     */
    List<TagVO> list();

    /**
     * 创建标签
     */
    void create(TagDTO dto);

    /**
     * 更新标签
     */
    void update(Long id, TagDTO dto);

    /**
     * 删除标签（同时删除关联关系）
     */
    void delete(Long id);
}
