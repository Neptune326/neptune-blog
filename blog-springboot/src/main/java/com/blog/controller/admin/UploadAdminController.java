package com.blog.controller.admin;

import com.blog.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传接口
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/upload")
public class UploadAdminController {

    @Value("${blog.upload.path:./uploads/}")
    private String uploadPath;

    @Value("${blog.upload.url-prefix:http://localhost:8080/uploads/}")
    private String urlPrefix;

    /** 允许的图片类型 */
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );

    /** 最大文件大小：5MB */
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    /**
     * 上传图片
     * POST /api/admin/upload/image
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 校验文件
        if (file.isEmpty()) {
            return Result.error(com.blog.common.result.ResultCode.BAD_REQUEST, "文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            return Result.error(com.blog.common.result.ResultCode.BAD_REQUEST, "文件大小不能超过 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            return Result.error(com.blog.common.result.ResultCode.BAD_REQUEST, "只支持 JPG、PNG、GIF、WebP、SVG 格式");
        }

        // 按日期分目录存储
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String dirPath = uploadPath + dateDir + "/";
        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("创建上传目录失败: {}", dirPath);
            return Result.error(com.blog.common.result.ResultCode.INTERNAL_ERROR, "上传目录创建失败");
        }

        // 生成唯一文件名
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".jpg";
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
        String filePath = dirPath + fileName;

        try {
            file.transferTo(new File(filePath));
            String url = urlPrefix + dateDir + "/" + fileName;
            log.info("文件上传成功: {}", url);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error(com.blog.common.result.ResultCode.INTERNAL_ERROR, "文件上传失败");
        }
    }
}
