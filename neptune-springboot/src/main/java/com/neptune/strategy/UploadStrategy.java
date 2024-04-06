package com.neptune.strategy;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.neptune.config.properties.MinioProperties;
import com.neptune.exception.ApiException;
import com.neptune.utils.CommonUtils;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Slf4j
@Service
public class UploadStrategy {

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private MinioProperties minioProperties;

    /*@PostConstruct
    public void init() {
        for (String bucketName : MinioConstant.BUCKETS) {
            existBucketOrBuild(bucketName);
        }
    }*/

    public Boolean existBucketOrBuild(String bucketName) {
        boolean existBucket = false;
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                .bucket(bucketName)
                .build();
        try {
            existBucket = minioClient.bucketExists(bucketExistsArgs);
            if (!existBucket) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build();
                minioClient.makeBucket(makeBucketArgs);
            } else {
                log.info("Bucket:{} 已存在", bucketName);
            }
        } catch (Exception e) {
            log.error("创建Bucket：{}失败：信息：{}", bucketName, e.getMessage());
        }
        return existBucket;
    }

    public Boolean existObjectName(String bucketName, String objectName) {
        boolean exist = true;
        StatObjectArgs statObjectArgs = StatObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        try {
            minioClient.statObject(statObjectArgs);
        } catch (Exception e) {
            exist = false;
        }
        return exist;
    }

    public String getPrivateAccessUrl(String bucketName, String objectName) {
        GetPresignedObjectUrlArgs urlArgs = GetPresignedObjectUrlArgs
                .builder()
                .bucket(bucketName)
                .object(objectName)
                .method(Method.GET)
                .build();
        String url = null;
        try {
            url = minioClient.getPresignedObjectUrl(urlArgs);
        } catch (Exception e) {
            log.error("获取文件路径失败：{}", e.getMessage());
        }
        return url;
    }

    public String upload(MultipartFile file, String bucketName) {
        Assert.notNull(file, "上传文件不能为空");
        String originalFilename = file.getOriginalFilename();
        Assert.isTrue(StrUtil.isNotBlank(originalFilename), "上传文件名称不能为空");
        String fileName = CommonUtils.snowflakeId() + originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = DateUtil.format(new Date(), "yyyyMMdd") + "/" + fileName;

        try {
            PutObjectArgs objectArgs = PutObjectArgs
                    .builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioClient.putObject(objectArgs);
        } catch (Exception e) {
            log.error("上传文件失败");
            throw new ApiException("上传文件失败");
        }
        return objectName;

    }

    public String uploadAndGetUrl(MultipartFile file, String bucketName) {
        String objectName = upload(file, bucketName);
        return minioProperties.getUrl() + "/" + bucketName + "/" + objectName;
    }


}
