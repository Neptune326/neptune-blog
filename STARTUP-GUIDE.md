# 项目启动说明

## 一、项目结构

```
neptune-blog/
├── blog-springboot/     # 后端 Spring Boot 项目
├── blog-vue/            # 前端 Vue 3 项目
├── GIT-GUIDE.md         # Git 使用指南
├── CICD-JENKINS.md      # CI/CD 部署指南
└── STARTUP-GUIDE.md     # 本文件
```

---

## 二、环境要求

| 工具 | 版本要求 | 验证命令 |
|------|---------|---------|
| JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| MySQL | 8.0+ | `mysql --version` |
| Node.js | 18+ | `node -v` |
| npm | 9+ | `npm -v` |

---

## 三、数据库初始化

### 1. 启动 MySQL 并创建数据库

```bash
mysql -u root -p
```

### 2. 执行初始化 SQL 脚本

```bash
mysql -u root -p < blog-springboot/src/main/resources/sql/init.sql
```

脚本会自动：
- 创建 `blog` 数据库
- 创建所有业务表（admin、article、category、tag、article_tag、comment）
- 插入初始管理员账号（用户名：`admin`，密码：`admin123`）

---

## 四、启动后端（blog-springboot）

### 开发环境

```bash
cd blog-springboot

# 方式一：Maven 直接运行
mvn spring-boot:run

# 方式二：先打包再运行
mvn clean package -DskipTests
java -jar target/blog-springboot-1.0.0.jar
```

默认使用 `dev` 环境配置（`application-dev.yml`），连接本地 MySQL。

### 切换环境

```bash
# 使用测试环境配置
java -jar target/blog-springboot-1.0.0.jar --spring.profiles.active=test

# 使用正式环境配置
java -jar target/blog-springboot-1.0.0.jar --spring.profiles.active=prod
```

### 验证后端启动成功

浏览器访问：`http://localhost:8080/api/articles`

应返回：
```json
{"code":200,"message":"success","data":{"total":0,"pages":0,"list":[]}}
```

---

## 五、启动前端（blog-vue）

### 1. 安装依赖

```bash
cd blog-vue
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

默认启动在 `http://localhost:5173`，已配置代理将 `/api` 请求转发到 `http://localhost:8080`。

### 3. 构建生产包

```bash
npm run build
```

构建产物在 `blog-vue/dist/` 目录，部署到 Nginx 静态目录即可。

---

## 六、访问地址

| 地址 | 说明 |
|------|------|
| `http://localhost:5173` | 前台博客首页 |
| `http://localhost:5173/admin/login` | 后台管理登录页 |
| `http://localhost:8080/api/articles` | 后端前台 API 示例 |
| `http://localhost:8080/api/admin/dashboard` | 后端后台 API 示例（需登录） |

---

## 七、默认账号

| 账号 | 密码 |
|------|------|
| admin | admin123 |

> 正式上线前请务必修改默认密码。

---

## 八、多环境配置说明

后端配置文件位于 `blog-springboot/src/main/resources/`：

| 文件 | 说明 |
|------|------|
| `application.yml` | 公共配置，通过 `spring.profiles.active` 指定激活环境 |
| `application-dev.yml` | 开发环境（默认激活） |
| `application-test.yml` | 测试环境（与开发环境一致） |
| `application-prod.yml` | 正式环境（数据库信息留空，需手动填写或通过环境变量注入） |

修改正式环境数据库配置：

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://your-db-host:3306/blog?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your-db-username
    password: your-db-password
```

> **安全提示**：`application-prod.yml` 已加入 `.gitignore`，不会提交到代码仓库。

---

## 九、运行测试

### 后端属性测试（jqwik）

```bash
cd blog-springboot
mvn test
```

### 前端属性测试（fast-check + vitest）

```bash
cd blog-vue
npm run test
```

---

## 十、常见问题

**Q：后端启动报错 `Communications link failure`**

A：MySQL 未启动或连接配置有误，检查 `application-dev.yml` 中的数据库地址、用户名、密码。

**Q：前端页面空白或接口 404**

A：确认后端已启动在 8080 端口，前端 `vite.config.js` 中的代理配置指向 `http://localhost:8080`。

**Q：登录提示用户名或密码错误**

A：确认已执行数据库初始化脚本，初始账号为 `admin` / `admin123`。

**Q：前端构建后路由刷新 404**

A：需要在 Nginx 中配置 `try_files $uri $uri/ /index.html`，参考 `CICD-JENKINS.md` 中的 Nginx 配置。
