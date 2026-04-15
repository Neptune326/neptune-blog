package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论 Mapper 接口
 * 继承 BaseMapper 获得基础 CRUD 操作
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
