package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.entity.VisitLog;
import com.blog.mapper.VisitLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 访问统计查询接口
 */
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class VisitStatController {

    private final VisitLogMapper visitLogMapper;

    /**
     * 获取最近 N 天的访问统计
     */
    @GetMapping("/visit")
    public Result<List<VisitLog>> visitStats(@RequestParam(defaultValue = "30") Integer days) {
        LocalDate start = LocalDate.now().minusDays(days - 1);
        List<VisitLog> list = visitLogMapper.selectList(
                new LambdaQueryWrapper<VisitLog>()
                        .isNull(VisitLog::getArticleId)
                        .ge(VisitLog::getVisitDate, start)
                        .orderByAsc(VisitLog::getVisitDate)
        );
        return Result.success(list);
    }
}
