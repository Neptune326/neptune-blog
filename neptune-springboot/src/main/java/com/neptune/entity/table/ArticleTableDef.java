package com.neptune.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 文章表 表定义层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public class ArticleTableDef extends TableDef {

    /**
     * 文章表
     */
    public static final ArticleTableDef ARTICLE = new ArticleTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 封面
     */
    public final QueryColumn COVER = new QueryColumn(this, "cover");

    /**
     * 是否置顶 0-否 1-是
     */
    public final QueryColumn IS_TOP = new QueryColumn(this, "is_top");

    /**
     * 标题
     */
    public final QueryColumn TITLE = new QueryColumn(this, "title");

    /**
     * 状态 0-下架 1-发布
     */
    public final QueryColumn STATUS = new QueryColumn(this, "status");

    /**
     * 内容
     */
    public final QueryColumn CONTENT = new QueryColumn(this, "content");

    /**
     * 删除标识 0-正常 1-删除
     */
    public final QueryColumn DEL_FLAG = new QueryColumn(this, "del_flag");

    /**
     * 文章简介
     */
    public final QueryColumn SUMMARY = new QueryColumn(this, "summary");

    /**
     * 乐观锁
     */
    public final QueryColumn VERSION = new QueryColumn(this, "version");

    /**
     * 创建人ID
     */
    public final QueryColumn CREATE_BY = new QueryColumn(this, "create_by");

    /**
     * 文章阅读量
     */
    public final QueryColumn QUANTITY = new QueryColumn(this, "quantity");

    /**
     * 更新人ID
     */
    public final QueryColumn UPDATE_BY = new QueryColumn(this, "update_by");

    /**
     * 分类ID
     */
    public final QueryColumn CATEGORY_ID = new QueryColumn(this, "category_id");

    /**
     * 创建时间
     */
    public final QueryColumn CREATE_TIME = new QueryColumn(this, "create_time");

    /**
     * 是否原创  0-转载 1-原创
     */
    public final QueryColumn IS_ORIGINAL = new QueryColumn(this, "is_original");

    /**
     * 更新时间
     */
    public final QueryColumn UPDATE_TIME = new QueryColumn(this, "update_time");

    /**
     * 转载地址
     */
    public final QueryColumn ORIGINAL_URL = new QueryColumn(this, "original_url");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, CREATE_BY, CREATE_TIME, UPDATE_BY, UPDATE_TIME, VERSION, STATUS, CATEGORY_ID, TITLE, COVER, SUMMARY, CONTENT, IS_TOP, IS_ORIGINAL, ORIGINAL_URL, QUANTITY};

    public ArticleTableDef() {
        super("", "t_article");
    }

}
