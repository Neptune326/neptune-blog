package com.neptune.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 权限资源表 表定义层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public class ResourceTableDef extends TableDef {

    /**
     * 权限资源表
     */
    public static final ResourceTableDef RESOURCE = new ResourceTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 资源编码
     */
    public final QueryColumn CODE = new QueryColumn(this, "code");

    /**
     * 资源名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 排序
     */
    public final QueryColumn SORT = new QueryColumn(this, "sort");

    /**
     * 所属菜单ID
     */
    public final QueryColumn MENU_ID = new QueryColumn(this, "menu_id");

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
     * 资源编码全称
     */
    public final QueryColumn FULL_CODE = new QueryColumn(this, "full_code");

    /**
     * 所属菜单名称
     */
    public final QueryColumn MENU_NAME = new QueryColumn(this, "menu_name");

    /**
     * 更新人ID
     */
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, VERSION, NAME, CODE, FULL_CODE, SORT, MENU_ID, MENU_NAME};

    public ResourceTableDef() {
        super("", "t_resource");
    }

}
