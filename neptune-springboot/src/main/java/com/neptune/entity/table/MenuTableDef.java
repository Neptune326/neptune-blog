package com.neptune.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 菜单表 表定义层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public class MenuTableDef extends TableDef {

    /**
     * 菜单表
     */
    public static final MenuTableDef MENU = new MenuTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 菜单编码
     */
    public final QueryColumn CODE = new QueryColumn(this, "code");

    /**
     * 菜单图标
     */
    public final QueryColumn ICON = new QueryColumn(this, "icon");

    /**
     * 菜单名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 排序
     */
    public final QueryColumn SORT = new QueryColumn(this, "sort");

    /**
     * 类型：0-菜单 1-目录
     */
    public final QueryColumn TYPE = new QueryColumn(this, "type");

    /**
     * 层级
     */
    public final QueryColumn LEVEL = new QueryColumn(this, "level");

    /**
     * 路由
     */
    public final QueryColumn ROUTE = new QueryColumn(this, "route");

    /**
     * 状态 0-正常 1-禁用
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

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
     * 是否隐藏 0-是 1-否
     */
    public final QueryColumn IS_HIDDEN = new QueryColumn(this, "is_hidden");

    /**
     * 父级菜单ID
     */
    public final QueryColumn PARENT_ID = new QueryColumn(this, "parent_id");

    /**
     * 更新人ID
     */
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 菜单ID全路径
     */
    public final QueryColumn MENU_ID_PATH = new QueryColumn(this, "menu_id_path");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 菜单名称全路径
     */
    public final QueryColumn MENU_NAME_PATH = new QueryColumn(this, "menu_name_path");

    /**
     * 组件名称
     */
    public final QueryColumn COMPONENT_NAME = new QueryColumn(this, "component_name");

    /**
     * 组件路径
     */
    public final QueryColumn COMPONENT_PATH = new QueryColumn(this, "component_path");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, VERSION, STATUS, TYPE, NAME, CODE, ROUTE, COMPONENT_NAME, COMPONENT_PATH, ICON, SORT, PARENT_ID, LEVEL, IS_HIDDEN, MENU_ID_PATH, MENU_NAME_PATH};

    public MenuTableDef() {
        super("", "t_menu");
    }

}
