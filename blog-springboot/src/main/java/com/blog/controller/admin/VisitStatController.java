package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.common.result.Result;
import com.blog.entity.VisitLog;
import com.blog.mapper.VisitLogMapper;
import com.blog.vo.VisitStatVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 访问统计查询接口
 */
@RestController
@RequestMapping("/api/admin/stats")
@Validated
@RequiredArgsConstructor
public class VisitStatController {

    private final VisitLogMapper visitLogMapper;

    @GetMapping("/visit")
    public Result<List<VisitStatVO>> visitStats(@RequestParam(defaultValue = "30") Integer days) {
        LocalDate start = LocalDate.now().minusDays(days - 1);
        List<VisitLog> list = visitLogMapper.selectList(
                new LambdaQueryWrapper<VisitLog>()
                        .isNull(VisitLog::getArticleId)
                        .ge(VisitLog::getVisitDate, start)
                        .orderByAsc(VisitLog::getVisitDate)
        );
        return Result.success(list.stream().map(this::toVisitStatVO).collect(Collectors.toList()));
    }

    private VisitStatVO toVisitStatVO(VisitLog visitLog) {
        VisitStatVO visitStatVO = new VisitStatVO();
        BeanUtils.copyProperties(visitLog, visitStatVO);
        return visitStatVO;
    }
}