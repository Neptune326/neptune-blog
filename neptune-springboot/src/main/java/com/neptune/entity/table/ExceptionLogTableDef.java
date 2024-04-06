package com.neptune.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 异常日志表 表定义层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public class ExceptionLogTableDef extends TableDef {

    /**
     * 异常日志表
     */
    public static final ExceptionLogTableDef EXCEPTION_LOG = new ExceptionLogTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 请求接口
     */
    public final QueryColumn OPT_URI = new QueryColumn(this, "opt_uri");

    /**
     * 删除标识 0-正常 1-删除
     */
    public final QueryColumn DEL_FLAG = new QueryColumn(this, "del_flag");

    /**
     * 乐观锁
     */
    public final QueryColumn VERSION = new QueryColumn(this, "version");

    /**
     * 创建人ID
     */
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    /**
     * IP来源
     */
    public final QueryColumn IP_SOURCE = new QueryColumn(this, "ip_source");

    /**
     * 更新人ID
     */
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    /**
     * IP
     */
    public final QueryColumn IP_ADDRESS = new QueryColumn(this, "ip_address");

    /**
     * 请求方式
     */
    public final QueryColumn OPT_METHOD = new QueryColumn(this, "opt_method");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 请求参数
     */
    public final QueryColumn REQUEST_PARAM = new QueryColumn(this, "request_param");

    /**
     * 异常信息
     */
    public final QueryColumn EXCEPTION_INFO = new QueryColumn(this, "exception_info");

    /**
     * 请求方式
     */
    public final QueryColumn REQUEST_METHOD = new QueryColumn(this, "request_method");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, VERSION, OPT_URI, OPT_METHOD, REQUEST_METHOD, REQUEST_PARAM, EXCEPTION_INFO, IP_ADDRESS, IP_SOURCE};

    public ExceptionLogTableDef() {
        super("", "t_exception_log");
    }

}
