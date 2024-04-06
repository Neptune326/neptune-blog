package com.neptune.mybatisflex;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.mybatisflex.annotation.InsertListener;

import java.time.LocalDateTime;

public class EntityInsertListener implements InsertListener {
    @Override
    public void onInsert(Object o) {
        if (StpUtil.isLogin()) {
            BeanUtil.setProperty(o, "createBy", StpUtil.getLoginIdAsLong());
        }
        BeanUtil.setProperty(o, "createTime", LocalDateTime.now());
    }
}
