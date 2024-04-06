package com.neptune.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.neptune.entity.OperationLog;
import com.neptune.entity.ResponseResult;
import com.neptune.service.OperationLogService;
import com.neptune.vo.admin.OperationLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.neptune.entity.table.OperationLogTableDef.OPERATION_LOG;
import static com.neptune.entity.table.UserTableDef.USER;


@RestController
@RequestMapping("/admin/operationLog")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @SaCheckLogin
    @GetMapping("info")
    public ResponseResult<OperationLogVo> getInfo(@RequestParam Long id) {
        OperationLog t = operationLogService.getById(id);
        OperationLogVo vo = BeanUtil.copyProperties(t, OperationLogVo.class);
        return ResponseResult.success(vo);
    }

    @SaCheckLogin
    @GetMapping("page")
    public ResponseResult<Page<OperationLogVo>> page(Page<OperationLogVo> page) {
        QueryWrapper qw = QueryWrapper.create()
                .select(OPERATION_LOG.ALL_COLUMNS, USER.USER_NAME.as(OperationLogVo::getCreateUser))
                .from(OPERATION_LOG)
                .leftJoin(USER).on(USER.ID.eq(OPERATION_LOG.CREATE_BY))
                .orderBy(OPERATION_LOG.CREATE_TIME.desc());
        return ResponseResult.success(operationLogService.pageAs(page, qw, OperationLogVo.class));
    }

}
