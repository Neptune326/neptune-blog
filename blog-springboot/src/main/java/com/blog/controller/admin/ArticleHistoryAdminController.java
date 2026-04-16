package com.blog.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.entity.Article;
import com.blog.entity.ArticleHistory;
import com.blog.mapper.ArticleHistoryMapper;
import com.blog.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章版本历史接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class ArticleHistoryAdminController {

    private final ArticleHistoryMapper historyMapper;
    private final ArticleMapper articleMapper;

    /** 查询文章版本历史列表 */
    @GetMapping("/{id}/history")
    public Result<List<ArticleHistory>> history(@PathVariable Long id) {
        return Result.success(historyMapper.selectList(
                new LambdaQueryWrapper<ArticleHistory>()
                        .eq(ArticleHistory::getArticleId, id)
                        .orderByDesc(ArticleHistory::getVersion)
        ));
    }

    /** 手动保存当前版本 */
    @PostMapping("/{id}/history")
    public Result<Void> saveVersion(@PathVariable Long id,
                                    @RequestParam(required = false) String remark) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Result.error(com.blog.common.result.ResultCode.ARTICLE_NOT_FOUND);
        }
        // 获取当前最大版本号
        ArticleHistory latest = historyMapper.selectOne(
                new LambdaQueryWrapper<ArticleHistory>()
                        .eq(ArticleHistory::getArticleId, id)
                        .orderByDesc(ArticleHistory::getVersion)
                        .last("LIMIT 1")
        );
        int nextVersion = (latest != null ? latest.getVersion() : 0) + 1;

        ArticleHistory history = new ArticleHistory();
        history.setArticleId(id);
        history.setTitle(article.getTitle());
        history.setContent(article.getContent());
        history.setVersion(nextVersion);
        history.setRemark(remark != null ? remark : "手动保存 v" + nextVersion);
        history.setCreateTime(LocalDateTime.now());
        try { history.setCreateBy((String) StpUtil.getLoginId()); } catch (Exception e) {}
        historyMapper.insert(history);
        log.info("文章 {} 保存版本 v{}", id, nextVersion);
        return Result.success();
    }

    /** 回滚到指定版本 */
    @PostMapping("/{id}/history/{historyId}/rollback")
    public Result<Void> rollback(@PathVariable Long id, @PathVariable Long historyId) {
        ArticleHistory history = historyMapper.selectById(historyId);
        if (history == null || !history.getArticleId().equals(id)) {
            return Result.error(com.blog.common.result.ResultCode.NOT_FOUND);
        }
        // 先保存当前版本
        Article current = articleMapper.selectById(id);
        if (current != null) {
            ArticleHistory backup = new ArticleHistory();
            backup.setArticleId(id);
            backup.setTitle(current.getTitle());
            backup.setContent(current.getContent());
            ArticleHistory latest = historyMapper.selectOne(
                    new LambdaQueryWrapper<ArticleHistory>()
                            .eq(ArticleHistory::getArticleId, id)
                            .orderByDesc(ArticleHistory::getVersion).last("LIMIT 1")
            );
            backup.setVersion((latest != null ? latest.getVersion() : 0) + 1);
            backup.setRemark("回滚前自动备份");
            backup.setCreateTime(LocalDateTime.now());
            historyMapper.insert(backup);
        }
        // 回滚
        Article update = new Article();
        update.setId(id);
        update.setTitle(history.getTitle());
        update.setContent(history.getContent());
        articleMapper.updateById(update);
        log.info("文章 {} 回滚到版本 v{}", id, history.getVersion());
        return Result.success();
    }

    /** 删除指定版本 */
    @DeleteMapping("/{id}/history/{historyId}")
    public Result<Void> deleteHistory(@PathVariable Long id, @PathVariable Long historyId) {
        historyMapper.deleteById(historyId);
        return Result.success();
    }
}
