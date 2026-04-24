package com.blog.controller.admin;

import com.blog.common.result.Result;
import com.blog.common.result.ResultCode;
import com.blog.service.SysConfigService;
import com.blog.vo.UploadFileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * File upload controller.
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/upload")
@Validated
@RequiredArgsConstructor
public class UploadAdminController {

    private static final String IMAGE_MAX_SIZE_MB_KEY = "upload_image_max_size_mb";
    private static final String IMAGE_ALLOWED_EXTS_KEY = "upload_image_allowed_exts";
    private static final String FILE_MAX_SIZE_MB_KEY = "upload_file_max_size_mb";
    private static final String FILE_ALLOWED_EXTS_KEY = "upload_file_allowed_exts";
    private static final int DEFAULT_IMAGE_MAX_SIZE_MB = 5;
    private static final int DEFAULT_FILE_MAX_SIZE_MB = 10;
    private static final String DEFAULT_IMAGE_ALLOWED_EXTS = ".jpg,.jpeg,.png,.gif,.webp,.svg";
    private static final String DEFAULT_FILE_ALLOWED_EXTS = ".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.md,.zip,.rar,.7z";
    private static final DateTimeFormatter DATE_DIR_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM");

    private final SysConfigService sysConfigService;

    @Value("${blog.upload.path:./uploads/}")
    private String uploadPath;

    @Value("${blog.upload.url-prefix:http://localhost:8080/uploads/}")
    private String urlPrefix;

    /**
     * Upload image and return URL string for existing callers.
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        UploadResult uploadResult = upload(file, IMAGE_MAX_SIZE_MB_KEY, DEFAULT_IMAGE_MAX_SIZE_MB,
                IMAGE_ALLOWED_EXTS_KEY, DEFAULT_IMAGE_ALLOWED_EXTS);
        if (uploadResult.error != null) {
            return errorResult(uploadResult.error);
        }
        return Result.success(uploadResult.file.getUrl());
    }

    /**
     * Upload generic file.
     */
    @PostMapping("/file")
    public Result<UploadFileVO> uploadFile(@RequestParam("file") MultipartFile file) {
        UploadResult uploadResult = upload(file, FILE_MAX_SIZE_MB_KEY, DEFAULT_FILE_MAX_SIZE_MB,
                FILE_ALLOWED_EXTS_KEY, DEFAULT_FILE_ALLOWED_EXTS);
        if (uploadResult.error != null) {
            return uploadResult.error;
        }
        return Result.success(uploadResult.file);
    }

    private UploadResult upload(MultipartFile file, String maxSizeKey, int defaultMaxSizeMb,
                                String allowedExtsKey, String defaultAllowedExts) {
        if (file == null || file.isEmpty()) {
            return UploadResult.error(Result.error(ResultCode.BAD_REQUEST, "文件不能为空"));
        }

        int maxSizeMb = sysConfigService.getIntValue(maxSizeKey, defaultMaxSizeMb);
        long maxSizeBytes = maxSizeMb * 1024L * 1024L;
        if (file.getSize() > maxSizeBytes) {
            return UploadResult.error(Result.error(ResultCode.BAD_REQUEST,
                    "文件大小不能超过 " + maxSizeMb + "MB"));
        }

        String originalName = file.getOriginalFilename();
        String extension = extractExtension(originalName);
        Set<String> allowedExts = parseAllowedExts(sysConfigService.getValue(allowedExtsKey), defaultAllowedExts);
        if (!allowedExts.contains(extension)) {
            log.warn("[Upload] rejected file extension, originalName: {}, extension: {}, allowedExts: {}",
                    originalName, extension, allowedExts);
            return UploadResult.error(Result.error(ResultCode.BAD_REQUEST,
                    "文件类型不支持，允许类型: " + String.join(",", allowedExts)));
        }

        String dateDir = LocalDate.now().format(DATE_DIR_FORMATTER);
        File dir = resolveTargetDirectory(dateDir);
        if (!dir.exists() && !dir.mkdirs()) {
            log.error("[Upload] failed to create directory: {}", dir.getAbsolutePath());
            return UploadResult.error(Result.error(ResultCode.INTERNAL_ERROR, "上传目录创建失败"));
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        File destFile = new File(dir, fileName);

        try {
            file.transferTo(destFile);
            UploadFileVO uploadFile = new UploadFileVO();
            uploadFile.setUrl(buildUrl(dateDir, fileName));
            uploadFile.setOriginalName(originalName == null ? "" : originalName);
            uploadFile.setFileName(fileName);
            uploadFile.setSize(file.getSize());
            uploadFile.setContentType(file.getContentType());
            uploadFile.setExtension(extension);
            log.info("[Upload] uploaded file, originalName: {}, fileName: {}, size: {}",
                    originalName, fileName, file.getSize());
            return UploadResult.success(uploadFile);
        } catch (IOException e) {
            log.error("[Upload] failed to save file, originalName: {}", originalName, e);
            return UploadResult.error(Result.error(ResultCode.INTERNAL_ERROR, "文件上传失败"));
        }
    }

    private File resolveTargetDirectory(String dateDir) {
        String basePath = uploadPath.endsWith("/") || uploadPath.endsWith("\\")
                ? uploadPath : uploadPath + "/";
        File baseDir = new File(basePath).getAbsoluteFile();
        return new File(baseDir, dateDir);
    }

    private String extractExtension(String originalName) {
        if (originalName == null) {
            return "";
        }
        int index = originalName.lastIndexOf('.');
        if (index < 0 || index == originalName.length() - 1) {
            return "";
        }
        return originalName.substring(index).toLowerCase(Locale.ROOT);
    }

    private Set<String> parseAllowedExts(String configuredValue, String defaultValue) {
        String value = configuredValue == null || configuredValue.isBlank() ? defaultValue : configuredValue;
        Set<String> allowedExts = new LinkedHashSet<>();
        for (String item : value.split(",")) {
            String ext = item.trim().toLowerCase(Locale.ROOT);
            if (!ext.isEmpty()) {
                allowedExts.add(ext);
            }
        }
        return allowedExts;
    }

    private String buildUrl(String dateDir, String fileName) {
        String prefix = urlPrefix.endsWith("/") ? urlPrefix : urlPrefix + "/";
        return prefix + dateDir + "/" + fileName;
    }

    private <T> Result<T> errorResult(Result<?> error) {
        ResultCode resultCode = ResultCode.INTERNAL_ERROR.getCode().equals(error.getCode())
                ? ResultCode.INTERNAL_ERROR : ResultCode.BAD_REQUEST;
        return Result.error(resultCode, error.getMessage());
    }

    private record UploadResult(Result<UploadFileVO> error, UploadFileVO file) {

        private static UploadResult success(UploadFileVO file) {
            return new UploadResult(null, file);
        }

        private static UploadResult error(Result<UploadFileVO> error) {
            return new UploadResult(error, null);
        }
    }
}
