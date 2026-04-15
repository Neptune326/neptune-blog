// 文章 2：Spring Boot 3.x + MyBatis-Plus 企业级最佳实践
export var content2 = `# Spring Boot 3.x + MyBatis-Plus 企业级最佳实践

## 前言

Spring Boot 3.x 基于 Spring Framework 6，要求 Java 17+，带来了更好的性能和原生镜像支持。本文将介绍如何在 Spring Boot 3.x 中整合 MyBatis-Plus，并建立一套完整的企业级开发规范。

> **环境要求**：JDK 17+、Spring Boot 3.2.x、MyBatis-Plus 3.5.x、MySQL 8.0+

## 项目依赖配置

\`\`\`xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
</parent>

<dependencies>
    <!-- Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- MyBatis-Plus（Spring Boot 3 专用） -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>3.5.7</version>
    </dependency>

    <!-- MySQL 驱动 -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Sa-Token 鉴权 -->
    <dependency>
        <groupId>cn.dev33</groupId>
        <artifactId>sa-token-spring-boot3-starter</artifactId>
        <version>1.37.0</version>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
\`\`\`

## 多环境配置

Spring Boot 支持通过 Profile 区分不同环境的配置：

\`\`\`yaml
# application.yml（公共配置）
spring:
  profiles:
    active: dev   # 默认激活开发环境
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

sa-token:
  token-name: Authorization
  timeout: 604800   # 7天
\`\`\`

\`\`\`yaml
# application-dev.yml（开发环境）
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog?useUnicode=true&characterEncoding=utf8
    username: root
    password: root

logging:
  level:
    com.blog: debug
\`\`\`

\`\`\`yaml
# application-prod.yml（生产环境，敏感信息留空）
spring:
  datasource:
    url:
    username:
    password:

logging:
  level:
    com.blog: info
  file:
    name: /var/log/blog/app.log
\`\`\`

## 统一响应封装

所有接口返回统一的 JSON 格式，便于前端统一处理：

\`\`\`java
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        return result;
    }
}
\`\`\`

业务错误码枚举：

\`\`\`java
public enum ResultCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    // 业务错误码
    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),
    CATEGORY_HAS_ARTICLES(1004, "该分类下存在文章，无法删除");

    private final Integer code;
    private final String message;
}
\`\`\`

## 全局异常处理

使用 \`@RestControllerAdvice\` 统一捕获异常：

\`\`\`java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getResultCode());
    }

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return Result.error(ResultCode.BAD_REQUEST, message);
    }

    // Sa-Token 未登录异常
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        return Result.error(ResultCode.UNAUTHORIZED);
    }

    // 兜底异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(ResultCode.INTERNAL_ERROR);
    }
}
\`\`\`

## MyBatis-Plus 自动填充

通过 \`MetaObjectHandler\` 自动填充审计字段，无需手动赋值：

\`\`\`java
@Component
public class BlogMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        String currentUser = getCurrentUser();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createBy", String.class, currentUser);
        this.strictInsertFill(metaObject, "updateBy", String.class, currentUser);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUser());
    }

    private String getCurrentUser() {
        try {
            if (StpUtil.isLogin()) {
                return (String) StpUtil.getLoginId();
            }
        } catch (Exception ignored) {}
        return "visitor";  // 前台访客操作
    }
}
\`\`\`

## Sa-Token 鉴权配置

\`\`\`java
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有后台接口，排除登录接口
        registry.addInterceptor(
            new SaInterceptor(handle -> StpUtil.checkLogin())
        )
        .addPathPatterns("/api/admin/**")
        .excludePathPatterns("/api/admin/auth/login");
    }
}
\`\`\`

## 分页查询

MyBatis-Plus 内置分页插件，配置后直接使用：

\`\`\`java
// 配置分页插件
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
}

// Service 层使用
public PageVO<ArticleListVO> adminList(Integer pageNum, Integer pageSize) {
    Page<ArticleListVO> page = new Page<>(pageNum, pageSize);
    articleMapper.selectArticleListVO(page, null, null, null, null);
    return PageVO.of(page);
}
\`\`\`

统一分页响应格式：

\`\`\`json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "pages": 10,
    "list": [...]
  }
}
\`\`\`

## 软删除

MyBatis-Plus 的 \`@TableLogic\` 注解实现逻辑删除，查询时自动过滤已删除记录：

\`\`\`java
@TableLogic
private Integer deleted;  // 0=正常，1=已删除
\`\`\`

配置后，\`deleteById\` 实际执行的是 \`UPDATE article SET deleted=1 WHERE id=?\`，而所有查询会自动加上 \`AND deleted=0\` 条件。

## CORS 跨域配置

\`\`\`java
@Bean
public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.addAllowedOriginPattern("*");
    config.addAllowedMethod("*");
    config.addAllowedHeader("*");
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
}
\`\`\`

## 总结

本文介绍了 Spring Boot 3.x + MyBatis-Plus 的企业级开发规范，包括：

- ✅ 多环境配置（dev/test/prod）
- ✅ 统一响应格式 \`Result<T>\`
- ✅ 全局异常处理器
- ✅ MyBatis-Plus 自动填充审计字段
- ✅ Sa-Token 路由鉴权
- ✅ 分页查询封装
- ✅ 软删除（逻辑删除）
- ✅ CORS 跨域配置

这套规范可以作为大多数 Spring Boot 项目的基础架构，按需扩展即可。
`
