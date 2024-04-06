package com.neptune.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 照片表 表定义层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public class PhotoTableDef extends TableDef {

    /**
     * 照片表
     */
    public static final PhotoTableDef PHOTO = new PhotoTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 路径
     */
    public final QueryColumn URL = new QueryColumn(this, "url");

    /**
     * 名称
     */
    public final QueryColumn NAME = new QueryColumn(this, "name");

    /**
     * 状态 0-正常 1-禁用
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 相册id
     */
    public final QueryColumn ALBUM_ID = new QueryColumn(this, "album_id");

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
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, VERSION, STATUS, ALBUM_ID, NAME, URL};

    public PhotoTableDef() {
        super("", "t_photo");
    }

}
