package com.blog.common.result;

/**
 * 业务错误码枚举
 * 定义系统中所有统一的响应状态码和描述信息
 */
public enum ResultCode {

    // ===== 通用状态码 =====
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // ===== 业务错误码（从 1001 开始）=====
    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),
    CATEGORY_NAME_DUPLICATE(1002, "分类名称已存在"),
    TAG_NAME_DUPLICATE(1003, "标签名称已存在"),
    CATEGORY_HAS_ARTICLES(1004, "该分类下存在文章，无法删除"),
    ARTICLE_NOT_FOUND(1005, "文章不存在"),
    COMMENT_ARTICLE_NOT_PUBLISHED(1006, "文章不存在或未发布");

    /** 状态码 */
    private final Integer code;

    /** 描述信息 */
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
