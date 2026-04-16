package com.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message")
public class Message extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String nickname;
    private String email;
    private String content;
    /** 0=待审核，1=已通过，2=已拒绝 */
    private Integer status;
}
