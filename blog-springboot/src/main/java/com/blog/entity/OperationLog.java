package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 操作人 */
    private String operator;
    /** 操作模块 */
    private String module;
    /** 操作描述 */
    private String action;
    /** HTTP 方法 */
    private String method;
    /** 请求路径 */
    private String requestUrl;
    /** 请求 IP */
    private String requestIp;
    /** 请求参数（JSON） */
    private String requestParam;
    /** 响应业务码 */
    private Integer responseCode;
    /** 耗时（ms） */
    private Integer costTime;
    /** 状态：1=成功，0=失败 */
    private Integer status;
    /** 操作时间 */
    private LocalDateTime createTime;
}
