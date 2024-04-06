package com.neptune.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 角色资源关联表 表定义层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public class RoleResourceTableDef extends TableDef {

    /**
     * 角色资源关联表
     */
    public static final RoleResourceTableDef ROLE_RESOURCE = new RoleResourceTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 角色ID
     */
    public final QueryColumn ROLE_ID = new QueryColumn(this, "role_id");

    /**
     * 资源ID
     */
    public final QueryColumn RESOURCE_ID = new QueryColumn(this, "resource_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ROLE_ID, RESOURCE_ID};

    public RoleResourceTableDef() {
        super("", "t_role_resource");
    }

}
