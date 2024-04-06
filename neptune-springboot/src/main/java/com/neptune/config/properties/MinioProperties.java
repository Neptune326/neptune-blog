package com.neptune.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(value = "minio", ignoreInvalidFields = true)
public class MinioProperties {

    private String url;

    private String accessKey;

    private String secretKey;

    private String bucketName;

}
