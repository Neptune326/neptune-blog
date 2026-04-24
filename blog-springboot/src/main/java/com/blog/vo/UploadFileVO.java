package com.blog.vo;

import lombok.Data;

/**
 * Uploaded file metadata.
 */
@Data
public class UploadFileVO {

    private String url;

    private String originalName;

    private String fileName;

    private Long size;

    private String contentType;

    private String extension;
}
