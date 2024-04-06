package com.neptune.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import com.neptune.mybatisflex.EntityInsertListener;
import com.neptune.mybatisflex.EntityUpdateListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章表 实体类。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_article", onInsert = EntityInsertListener.class, onUpdate = EntityUpdateListener.class)
public class Article implements Serializable {

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    /**
     * 创建人ID
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人ID
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 乐观锁
     */
    @Column(version = true)
    private Integer version;

    /**
     * 删除标识 0-正常 1-删除
     */
    @Column(isLogicDelete = true)
    private Integer delFlag;

    /**
     * 状态 0-下架 1-发布
     */
    private Integer status;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面
     */
    private String cover;

    /**
     * 文章简介
     */
    private String summary;

    /**
     * 内容
     */
    @Column(isLarge = true)
    private String content;

    /**
     * 是否置顶 0-否 1-是
     */
    private Integer isTop;

    /**
     * 是否原创  0-转载 1-原创
     */
    private Integer isOriginal;

    /**
     * 转载地址
     */
    private String originalUrl;

    /**
     * 文章阅读量
     */
    private Long quantity;

}
