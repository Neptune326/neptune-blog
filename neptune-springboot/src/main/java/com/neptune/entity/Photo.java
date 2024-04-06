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
 * 照片表 实体类。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_photo", onInsert = EntityInsertListener.class, onUpdate = EntityUpdateListener.class)
public class Photo implements Serializable {

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
     * 状态 0-正常 1-禁用
     */
    private Integer status;

    /**
     * 相册id
     */
    private Long albumId;

    /**
     * 名称
     */
    private String name;

    /**
     * 路径
     */
    private String url;

}
