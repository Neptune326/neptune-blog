package com.blog.common.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 在 INSERT/UPDATE 操作时自动填充审计字段（创建时间、更新时间、创建人、更新人）
 */
@Slf4j
@Component
public class BlogMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时自动填充
     * 填充 createTime、updateTime、createBy、updateBy
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        String currentUser = getCurrentUser();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createBy", String.class, currentUser);
        this.strictInsertFill(metaObject, "updateBy", String.class, currentUser);
    }

    /**
     * 更新时自动填充
     * 填充 updateTime、updateBy
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUser());
    }

    /**
     * 获取当前操作用户名
     * 后台管理操作（已登录）：返回 Sa-Token 登录用户名
     * 前台访客操作（未登录，如提交评论）：返回 "visitor"
     */
    private String getCurrentUser() {
        try {
            if (StpUtil.isLogin()) {
                return (String) StpUtil.getLoginId();
            }
        } catch (Exception ignored) {
            // Sa-Token 上下文不存在时忽略，返回默认值
        }
        return "visitor";
    }
}
