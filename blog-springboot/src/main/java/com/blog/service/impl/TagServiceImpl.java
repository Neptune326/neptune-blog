package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.dto.TagDTO;
import com.blog.entity.ArticleTag;
import com.blog.entity.Tag;
import com.blog.mapper.ArticleTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.TagService;
import com.blog.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;

    /**
     * 查询所有标签及文章数量
     */
    @Override
    public List<TagVO> list() {
        return tagMapper.selectTagWithArticleCount();
    }

    /**
     * 创建标签，检查名称重复
     */
    @Override
    public void create(TagDTO dto) {
        // 检查同名标签是否已存在
        long count = tagMapper.selectCount(
                new LambdaQueryWrapper<Tag>().eq(Tag::getName, dto.getName())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.TAG_NAME_DUPLICATE);
        }
        Tag tag = new Tag();
        tag.setName(dto.getName());
        tagMapper.insert(tag);
    }

    /**
     * 更新标签，检查名称重复（排除自身）
     */
    @Override
    public void update(Long id, TagDTO dto) {
        // 检查同名标签是否已存在（排除自身）
        long count = tagMapper.selectCount(
                new LambdaQueryWrapper<Tag>()
                        .eq(Tag::getName, dto.getName())
                        .ne(Tag::getId, id)
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.TAG_NAME_DUPLICATE);
        }
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(dto.getName());
        tagMapper.updateById(tag);
    }

    /**
     * 删除标签，先删除文章标签关联关系，再删除标签本身
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 删除文章与该标签的关联关系
        articleTagMapper.delete(
                new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, id)
        );
        // 删除标签本身
        tagMapper.deleteById(id);
    }
}
