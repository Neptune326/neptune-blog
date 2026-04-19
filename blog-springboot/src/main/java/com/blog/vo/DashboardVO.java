package com.blog.vo;

import lombok.Data;

/**
 * 仪表盘统计视图对象
 * 用于返回后台仪表盘的各项统计数据
 */
@Data
public class DashboardVO {

    /** 文章总数 */
    private Long articleCount;

    /** 分类总数 */
    private Long categoryCount;

    /** 标签总数 */
    private Long tagCount;

    /** 评论总数 */
    private Long commentCount;

    /** 待审核评论数 */
    private Long pendingCommentCount;

    /** 留言总数 */
    private Long messageCount;
}
