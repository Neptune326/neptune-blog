package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.entity.Comment;
import com.blog.mapper.ArticleMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.TagMapper;
import com.blog.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台仪表盘 Controller
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardAdminController {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final CommentMapper commentMapper;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping
    public Result<DashboardVO> dashboard() {
        DashboardVO vo = new DashboardVO();

        // 文章总数（MyBatis-Plus @TableLogic 自动过滤已删除记录，无需手动加条件）
        vo.setArticleCount(articleMapper.selectCount(null));

        // 分类总数
        vo.setCategoryCount(categoryMapper.selectCount(null));

        // 标签总数
        vo.setTagCount(tagMapper.selectCount(null));

        // 评论总数
        vo.setCommentCount(commentMapper.selectCount(null));

        // 待审核评论数
        vo.setPendingCommentCount(commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>().eq(Comment::getStatus, 0)
        ));

        return Result.success(vo);
    }
}
