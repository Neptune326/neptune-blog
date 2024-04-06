package com.neptune.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.neptune.entity.ExceptionLog;
import com.neptune.entity.ResponseResult;
import com.neptune.service.ExceptionLogService;
import com.neptune.vo.admin.ExceptionLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.neptune.entity.table.ExceptionLogTableDef.EXCEPTION_LOG;
import static com.neptune.entity.table.UserTableDef.USER;


@RestController
@RequestMapping("/admin/exceptionLog")
public class ExceptionLogController {

    @Autowired
    private ExceptionLogService exceptionLogService;

    @SaCheckLogin
    @GetMapping("info")
    public ResponseResult<ExceptionLogVo> getInfo(@RequestParam Long id) {
        ExceptionLog t = exceptionLogService.getById(id);
        ExceptionLogVo vo = BeanUtil.copyProperties(t, ExceptionLogVo.class);
        return ResponseResult.success(vo);
    }

    @SaCheckLogin
    @GetMapping("page")
    public ResponseResult<Page<ExceptionLogVo>> page(Page<ExceptionLogVo> page) {
        QueryWrapper qw = QueryWrapper.create()
                .select(EXCEPTION_LOG.ALL_COLUMNS, USER.USER_NAME.as(ExceptionLogVo::getCreateUser))
                .from(EXCEPTION_LOG)
                .leftJoin(USER).on(USER.ID.eq(EXCEPTION_LOG.CREATE_BY))
                .orderBy(EXCEPTION_LOG.CREATE_TIME.desc());
        return ResponseResult.success(exceptionLogService.pageAs(page, qw, ExceptionLogVo.class));
    }

}