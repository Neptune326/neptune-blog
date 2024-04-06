package com.neptune.mybatisflex;


import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MyBatisFlexCofig implements MyBatisFlexCustomizer {

    public MyBatisFlexCofig() {
        //开启审计功能
        AuditManager.setAuditEnable(true);

    }

    @Override
    public void customize(FlexGlobalConfig flexGlobalConfig) {
        flexGlobalConfig.setPrintBanner(false);
    }
}
