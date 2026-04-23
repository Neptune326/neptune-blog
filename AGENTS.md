# 全局项目规范（必须遵守）

本文件定义 `neptune-blog` 项目的编码与修改规范，适用于后端 `blog-springboot` 和前端 `blog-vue`。

你必须：

1. 修改代码前阅读并理解本文件。
2. 优先遵守本项目现有结构、命名、框架和工具链。
3. 修改完成后自检是否违反本规范。
4. 如本文件与更高优先级的系统/开发者指令冲突，以更高优先级指令为准；在项目规范内部冲突时，以本文件为准。
5. 不得为完成局部需求顺手重构无关代码，不得回滚用户或他人已有改动。

> 版本: 4.2 | 更新: 2026-04-23

---

## 项目结构

| 模块 | 路径 | 技术栈 |
|------|------|--------|
| 后端 | `blog-springboot` | Java 17、Spring Boot 3.2.x、MyBatis-Plus、MySQL、Sa-Token、Lombok |
| 前端 | `blog-vue` | Vue 3、Vite、Vuetify、Pinia、Vue Router、Axios、Tailwind CSS |

后端根包名固定为 `com.blog`。禁止引入其他项目的包名、工具类或中间件约定，例如 `com.dsl`、Nacos、RocketMQ、Doris、DmpSystemApi，除非项目代码已经实际接入并且需求明确要求。

---

## 通用编码规范

### 命名约定

| 类型 | 规则 | 示例 |
|------|------|------|
| Java 类/接口 | `UpperCamelCase` | `ArticleService` |
| Java 方法/字段 | `lowerCamelCase` | `getArticleById` |
| Java 常量 | `UPPER_SNAKE_CASE` | `MAX_BATCH_SIZE` |
| 数据库表/字段 | `lower_snake_case` | `article_series`、`create_time` |
| 后端接口路径 | 小写短横线或资源名复数 | `/api/admin/friend-links` |
| 前端组件 | `UpperCamelCase.vue` | `ArticleCard.vue` |
| 前端 API 文件 | `lowerCamelCase.js` 或业务名小写 | `sysConfig.js`、`article.js` |

### Import 规则

- 禁止在新代码中使用全限定类名，必须先 `import` 后使用简单类名。
- 静态成员如有必要使用 `import static`。
- 禁止通配符导入。
- 存量代码中已有全限定类名时，修改同一片代码应顺手清理；无关文件不强制改。

```java
import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;

if (article == null) {
    throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
}
```

### 日志规范

- 使用 `@Slf4j`。
- 使用占位符 `{}`，禁止字符串拼接。
- 业务日志要能看出业务场景，推荐格式：`[文章管理] 创建文章，title={}`。
- 不打印密码、Token、邮箱、手机号、身份证号等敏感信息；必须打印时先脱敏。
- `ERROR` 日志必须带堆栈：`log.error("系统异常", e)`。
- 不要求每一次数据库查询都打印日志；只在关键业务入口、写操作、外部调用、异常和关键分支处记录，避免噪声日志。

---

## 后端规范

### 类命名

| 类型 | 规则 | 示例 |
|------|------|------|
| 实体类 | 业务名 | `Article`、`ArticleSeries` |
| Controller | `*Controller`，按 `admin`/`frontend` 包区分 | `ArticleAdminController`、`ArticleFrontController` |
| Service 接口 | `*Service`，不使用 `I` 前缀 | `ArticleService` |
| Service 实现 | `*ServiceImpl` | `ArticleServiceImpl` |
| Mapper | `*Mapper` | `ArticleMapper` |
| 请求/传输对象 | `*DTO` | `ArticleDTO` |
| 响应视图对象 | `*VO` | `ArticleVO`、`PageVO` |
| 枚举类 | `*Enum` | `ArticleStatusEnum` |
| 异常/错误码 | 统一使用现有 `BusinessException`、`ResultCode` | `ResultCode.ARTICLE_NOT_FOUND` |

### 依赖注入

- 新代码使用 `@RequiredArgsConstructor` + `private final` 构造器注入。
- 禁止新增 `@Autowired` 字段注入。
- 存量代码不强制整体修改，但编辑同一类时应优先改为构造器注入。

```java
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
}
```

### Controller 层

Controller 只负责接收参数、参数校验、调用 Service、返回 `Result<T>`。业务判断、数据库访问、批量转换等逻辑应放到 Service。

统一返回类型：

```java
public Result<ArticleVO> getById(@PathVariable Long id)
public Result<PageVO<ArticleListVO>> list(...)
public Result<Void> create(@Valid @RequestBody ArticleDTO dto)
```

HTTP 方法遵循当前 REST 风格：

| 场景 | 方法 | 示例 |
|------|------|------|
| 单个资源查询 | GET | `GET /api/articles/{id}` |
| 列表/条件查询 | GET，复杂条件可 POST | `GET /api/admin/articles` |
| 新增 | POST | `POST /api/admin/articles` |
| 修改 | PUT/PATCH | `PUT /api/admin/articles/{id}` |
| 删除 | DELETE | `DELETE /api/admin/articles/{id}` |
| 批量操作 | 与语义匹配的 POST/PUT/DELETE | `PUT /api/admin/articles/batch-status` |

### 参数校验

- Controller 类添加 `@Validated`。
- 请求体参数使用 `@Valid @RequestBody`。
- DTO 字段使用 `jakarta.validation` 校验注解。
- 嵌套对象或集合元素需要加 `@Valid` 才能触发内部校验。
- 路径参数和查询参数需要校验时，配合 `@Validated` 使用 `@NotNull`、`@Min` 等注解。

```java
@Data
public class ArticleDTO {

    @NotBlank(message = "文章标题不能为空")
    private String title;

    @NotBlank(message = "文章正文不能为空")
    private String content;
}
```

### 异常处理

- 可预期的业务错误使用 `BusinessException` + `ResultCode`。
- 不直接抛出 `RuntimeException` 表达业务失败。
- 需要新增业务错误时，先在 `ResultCode` 中定义清晰的错误码和消息。
- 未预期系统异常交给 `GlobalExceptionHandler` 兜底并记录堆栈。

```java
if (article == null) {
    throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
}
```

### Service 层

- 使用卫语句减少嵌套。
- 方法尽量保持单一职责；超过约 50 行应考虑拆分私有方法。
- `if-else` 嵌套不超过 2 层；超过 3 个稳定业务分支时考虑策略、枚举分发或表驱动。
- 多表写操作必须加 `@Transactional(rollbackFor = Exception.class)`。
- 事务方法必须是 `public`，避免同类内部调用导致代理失效。
- 查询方法不得返回 Entity 给 Controller，应转换为 VO/DTO。
- 批量处理超过 1000 条时必须分批。

```java
if (article == null) {
    throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
}
if (!Objects.equals(article.getStatus(), 1)) {
    throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
}
```

### Mapper 与 SQL

- 简单 CRUD 使用 MyBatis-Plus Mapper/Lambda API。
- 复杂查询使用 `src/main/resources/mapper/*.xml`。
- SQL 参数使用 `#{}` 预编译，禁止用 `${}` 拼接用户输入。
- 动态排序必须白名单映射，不允许直接拼接前端传入字段。
- `.last(...)` 只能使用服务端常量或经过严格校验的值，不得拼接用户输入。
- 避免循环中查询数据库，优先批量查询后在内存关联。

```xml
ORDER BY
<choose>
    <when test="orderColumn == 'create_time'">create_time</when>
    <when test="orderColumn == 'update_time'">update_time</when>
    <otherwise>id</otherwise>
</choose>
```

### 实体与数据库表

实体继承 `BaseEntity` 时，表结构应包含以下审计字段：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `create_time` | `datetime` | 创建时间 |
| `create_by` | `varchar(50)` | 创建人 |
| `update_time` | `datetime` | 更新时间 |
| `update_by` | `varchar(50)` | 更新人 |

需要逻辑删除的业务表按现有项目风格增加：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| `deleted` | `tinyint` | 软删除标志：0=正常，1=已删除 |

建表语句优先维护 `blog-springboot/src/main/resources/sql/init.sql`。新增表、字段、索引应合并到最终结构，并在“后续变更追加区”记录日期和说明。

索引命名：

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 唯一索引 | `uk_业务_字段` | `uk_article_like_article_ip` |
| 普通索引 | `idx_业务_字段` | `idx_article_category_id` |
| 联合索引 | `idx_业务_字段1_字段2` | `idx_article_status_create_time` |

### 对象转换

- 禁止直接把 Entity 返回给前端。
- Entity、DTO、VO 转换可使用 `BeanUtils.copyProperties(source, target)` 或手写转换。
- `BeanUtils.copyProperties` 是浅拷贝，嵌套对象、集合和派生字段必须手动处理。
- 列表转换注意空集合处理，避免 NPE。

### 安全规范

- 密码必须使用 BCrypt 等安全哈希，禁止明文保存。
- 禁止在代码、配置文件或日志中写入真实数据库密码、第三方密钥、JWT 密钥、Token。
- 富文本或 Markdown 渲染相关内容必须考虑 XSS 清洗；复用现有 `XssUtils` 或统一过滤逻辑。
- 管理端接口必须遵循现有 Sa-Token 鉴权方案。
- 关键后台写操作使用 `@OperationLog` 记录操作日志。

### 缓存与并发

当前项目未统一接入 Redis/Redisson。新增缓存、分布式锁或消息队列前必须先确认依赖、配置和部署方案，不能只写业务代码假设组件存在。

如引入缓存：

- Key 格式：`blog:{业务}:{标识}`。
- 必须设置 TTL，禁止永不过期。
- 缓存空值需要短 TTL，防止穿透。
- 更新策略默认先更新数据库，再删除缓存。

### 性能要求

- 禁止明显的 N+1 查询。
- 深分页场景优先考虑游标分页或限制最大页码。
- 大数据量导出必须流式读取和分批写入，避免一次性加载到内存。
- 计数、点赞、阅读量等高频写入要考虑幂等、防刷和并发更新。

---

## 前端规范

### 基础约定

- 使用 Vue 3 Options API 或项目现有组件风格，不为局部需求强行切换整体范式。
- API 请求统一通过 `src/api/request.js` 或 `src/api/*.js` 封装，不在组件里重复创建 Axios 实例。
- 路由统一维护在 `src/router/index.js`。
- 登录态和跨页面状态优先使用 Pinia 或现有存储方案。
- 组件命名使用 `UpperCamelCase.vue`，公共组件放在 `src/components`。

### 组件与状态

- 页面组件负责组合视图和调用 API，复杂数据处理应拆到方法、组合函数或工具模块。
- 表单必须有前端校验和后端错误提示。
- 列表页要处理加载中、空数据、失败、分页等状态。
- 删除、批量操作、覆盖更新等危险操作必须有确认交互。
- 不在模板中写复杂表达式，复杂逻辑放到方法或计算属性。

### 样式与交互

- 优先沿用 Vuetify 和项目现有视觉风格。
- Tailwind CSS 可用于局部布局和工具类，不要与 Vuetify 组件样式互相覆盖到难以维护。
- 响应式布局必须兼容移动端和桌面端。
- 避免新增大面积无意义动效；已有动效组件要可配置、可关闭，并注意性能。
- 不引入未经确认的大体积前端依赖。

### 前端安全

- 渲染后端返回的 HTML/Markdown 前必须确认已清洗或使用安全渲染策略。
- Token 只通过现有请求拦截器注入，不在业务组件里手动拼接认证头。
- 不在前端代码中硬编码真实密钥、管理密码或私有接口凭证。

---

## 测试与验证

### 后端测试

- 后端测试使用 Maven：`mvn test`。
- 新增或修改 Service/Controller 逻辑时，应补充或更新 `*Test.java`。
- 涉及数据库结构时，同步维护测试 SQL 或 H2 兼容结构。
- 测试至少覆盖正常流程、参数错误、业务异常和关键边界条件。

### 前端测试

- 前端测试使用：`npm run test`。
- 前端构建验证使用：`npm run build`。
- 修改 API 封装、路由守卫、登录态、复杂表单时应补充或更新 Vitest 测试。

### 提交前自检

提交前至少检查：

| 检查点 | 要求 |
|--------|------|
| 命名 | 是否符合当前模块约定 |
| 返回值 | 后端是否统一使用 `Result<T>` |
| 异常 | 业务失败是否使用 `BusinessException` |
| 参数校验 | Controller/DTO 是否有必要校验 |
| SQL 安全 | 是否使用 `#{}`，是否避免拼接用户输入 |
| 事务 | 多表写操作是否有事务 |
| 空安全 | 集合、对象链路是否判空 |
| 幂等 | 写接口是否防重复提交或重复执行 |
| 日志 | 是否有关键日志，是否避免敏感信息 |
| 前端状态 | 是否处理加载、空数据和错误 |

---

## Git 与提交规范

- 修改前查看 `git status`，不要覆盖无关改动。
- 只提交与当前任务相关的文件。
- 提交信息使用 Conventional Commits：

```text
docs: update project agent guidelines
fix: correct article batch validation
feat: add comment audit filter
refactor: simplify article service conversion
```

- 如果用户明确要求提交，完成修改和必要检查后直接提交。
