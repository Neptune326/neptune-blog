package com.neptune.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 操作日志表 表定义层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public class OperationLogTableDef extends TableDef {

    /**
     * 操作日志表
     */
    public static final OperationLogTableDef OPERATION_LOG = new OperationLogTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 操作URI
     */
    public final QueryColumn OPT_URI = new QueryColumn(this, "opt_uri");

    /**
     * 删除标识 0-正常 1-删除
     */
    public final QueryColumn DEL_FLAG = new QueryColumn(this, "del_flag");

    /**
     * 操作类型
     */
    public final QueryColumn OPT_TYPE = new QueryColumn(this, "opt_type");

    /**
     * 乐观锁
     */
    public final QueryColumn VERSION = new QueryColumn(this, "version");

    /**
     * 创建人ID
     */
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    /**
     * 操作地址
     */
    public final QueryColumn IP_SOURCE = new QueryColumn(this, "ip_source");

    /**
     * 更新人ID
     */
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    /**
     * 操作IP
     */
    public final QueryColumn IP_ADDRESS = new QueryColumn(this, "ip_address");

    /**
     * 操作方法
     */
    public final QueryColumn OPT_METHOD = new QueryColumn(this, "opt_method");

    /**
     * 操作模块
     */
    public final QueryColumn OPT_MODULE = new QueryColumn(this, "opt_module");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 处理时长-毫秒
     */
    public final QueryColumn HANDLE_TIME = new QueryColumn(this, "handle_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 请求参数
     */
    public final QueryColumn REQUEST_PARAM = new QueryColumn(this, "request_param");

    /**
     * 返回数据
     */
    public final QueryColumn RESPONSE_DATA = new QueryColumn(this, "response_data");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, VERSION, OPT_MODULE, OPT_TYPE, OPT_URI, OPT_METHOD, REQUEST_METHOD, REQUEST_PARAM, RESPONSE_DATA, IP_ADDRESS, IP_SOURCE, HANDLE_TIME};

    public OperationLogTableDef() {
        super("", "t_operation_log");
    }

}
