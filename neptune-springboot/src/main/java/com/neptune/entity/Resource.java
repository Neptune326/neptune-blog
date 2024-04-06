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
 * 权限资源表 实体类。
 *
 * @author Neptune
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "t_resource", onInsert = EntityInsertListener.class, onUpdate = EntityUpdateListener.class)
public class Resource implements Serializable {

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
     * 资源名称
     */
    private String name;

    /**
     * 资源编码
     */
    private String code;

    /**
     * 资源编码全称
     */
    private String fullCode;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 所属菜单ID
     */
    private Long menuId;

    /**
     * 所属菜单名称
     */
    private String menuName;

}
