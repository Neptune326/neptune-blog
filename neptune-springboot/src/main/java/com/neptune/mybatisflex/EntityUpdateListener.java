package com.neptune.mybatisflex;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.annotation.UpdateListener;

import java.time.LocalDateTime;

public class EntityUpdateListener implements UpdateListener {

    @Override
    public void onUpdate(Object o) {
        if (StpUtil.isLogin()) {
            BeanUtil.setProperty(o, "updateBy", StpUtil.getLoginIdAsLong());
        }
        BeanUtil.setProperty(o, "updateTime", LocalDateTime.now());
    }
}
