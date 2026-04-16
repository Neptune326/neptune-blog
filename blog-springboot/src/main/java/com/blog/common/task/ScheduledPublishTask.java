package com.blog.common.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.blog.entity.Article;
import com.blog.mapper.ArticleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时发布任务
 * 每分钟检查一次，将到达发布时间的草稿自动发布
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledPublishTask {

    private final ArticleMapper articleMapper;

    /**
     * 每分钟执行一次，检查定时发布的文章
     */
    @Scheduled(fixedDelay = 60000)
    public void checkScheduledPublish() {
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 0)  // 草稿
                        .isNotNull(Article::getPublishTime)
                        .le(Article::getPublishTime, LocalDateTime.now())
        );

        if (!articles.isEmpty()) {
            for (Article article : articles) {
                articleMapper.update(null,
                        new LambdaUpdateWrapper<Article>()
                                .eq(Article::getId, article.getId())
                                .set(Article::getStatus, 1)
                                .set(Article::getPublishTime, null)
                );
                log.info("文章 [{}] 定时发布成功", article.getTitle());
            }
        }
    }
}
