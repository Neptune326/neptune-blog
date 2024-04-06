package com.neptune.entity.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

/**
 * 角色资源关联表 表定义层。
 *
 * @author Neptune
 * @since 1.0.0
 */
public class ArticleTagTableDef extends TableDef {

    /**
     * 角色资源关联表
     */
    public static final ArticleTagTableDef ARTICLE_TAG = new ArticleTagTableDef();

    /**
     * 主键ID
     */
    public final QueryColumn ID = new QueryColumn(this, "id");

    /**
     * 标签ID
     */
    public final QueryColumn TAG_ID = new QueryColumn(this, "tag_id");

    /**
     * 文章ID
     */
    public final QueryColumn ARTICLE_ID = new QueryColumn(this, "article_id");

    /**
     * 所有字段。
     */
    public final QueryColumn ALL_COLUMNS = new QueryColumn(this, "*");

    /**
     * 默认字段，不包含逻辑删除或者 large 等字段。
     */
    public final QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, ARTICLE_ID, TAG_ID};

    public ArticleTagTableDef() {
        super("", "t_article_tag");
    }

}
