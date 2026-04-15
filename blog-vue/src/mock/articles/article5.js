// 文章 5：Docker 容器化部署完整实践指南
export var content5 = `# Docker 容器化部署完整实践指南

## 前言

Docker 已经成为现代软件部署的标准方式。本文将从基础概念出发，带你完成一个前后端分离项目的完整容器化部署，包括 Spring Boot 后端、Vue 前端和 MySQL 数据库的编排。

> **环境要求**：Linux 服务器（Ubuntu 22.04），Docker 24+，Docker Compose 2.x

## Docker 核心概念

理解这三个核心概念是使用 Docker 的基础：

| 概念 | 说明 | 类比 |
|------|------|------|
| **镜像（Image）** | 只读的应用模板 | 程序安装包 |
| **容器（Container）** | 镜像的运行实例 | 运行中的程序 |
| **仓库（Registry）** | 存储和分发镜像 | 应用商店 |

## 安装 Docker

\`\`\`bash
# Ubuntu 安装 Docker
curl -fsSL https://get.docker.com | sh

# 将当前用户加入 docker 组（避免每次 sudo）
sudo usermod -aG docker $USER
newgrp docker

# 验证安装
docker --version
docker compose version
\`\`\`

## 后端 Dockerfile

为 Spring Boot 应用编写 Dockerfile：

\`\`\`dockerfile
# blog-springboot/Dockerfile

# 第一阶段：构建
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# 先下载依赖（利用 Docker 层缓存）
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# 第二阶段：运行（使用更小的基础镜像）
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 创建非 root 用户（安全最佳实践）
RUN addgroup -S blog && adduser -S blog -G blog
USER blog

# 从构建阶段复制 jar 包
COPY --from=builder /app/target/blog-springboot-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令（使用正式环境配置）
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
\`\`\`

**多阶段构建的优势**：
- 最终镜像不包含 Maven、源码等构建工具
- 镜像体积从 ~800MB 减小到 ~200MB

## 前端 Dockerfile

\`\`\`dockerfile
# blog-vue/Dockerfile

# 第一阶段：构建
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --registry=https://registry.npmmirror.com
COPY . .
RUN npm run build

# 第二阶段：Nginx 服务
FROM nginx:alpine
# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html
# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
\`\`\`

Nginx 配置文件 \`blog-vue/nginx.conf\`：

\`\`\`nginx
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # Vue Router history 模式支持
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 静态资源缓存
    location ~* \\.(js|css|png|jpg|gif|ico|svg)$ {
        expires 30d;
        add_header Cache-Control "public, immutable";
    }

    # Gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;
    gzip_min_length 1024;
}
\`\`\`

## docker-compose 编排

使用 docker-compose 统一管理所有服务：

\`\`\`yaml
# docker-compose.yml
version: '3.8'

services:
  # MySQL 数据库
  mysql:
    image: mysql:8.0
    container_name: blog-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: \${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: blog
      MYSQL_USER: \${MYSQL_USER}
      MYSQL_PASSWORD: \${MYSQL_PASSWORD}
    volumes:
      # 数据持久化
      - mysql_data:/var/lib/mysql
      # 初始化 SQL
      - ./blog-springboot/src/main/resources/sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "3306:3306"
    networks:
      - blog-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Spring Boot 后端
  backend:
    build:
      context: ./blog-springboot
      dockerfile: Dockerfile
    container_name: blog-backend
    restart: unless-stopped
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/blog?useUnicode=true&characterEncoding=utf8
      SPRING_DATASOURCE_USERNAME: \${MYSQL_USER}
      SPRING_DATASOURCE_PASSWORD: \${MYSQL_PASSWORD}
    ports:
      - "8080:8080"
    networks:
      - blog-network
    depends_on:
      mysql:
        condition: service_healthy
    volumes:
      - ./logs:/app/logs

  # Vue 前端（Nginx）
  frontend:
    build:
      context: ./blog-vue
      dockerfile: Dockerfile
    container_name: blog-frontend
    restart: unless-stopped
    ports:
      - "80:80"
    networks:
      - blog-network
    depends_on:
      - backend

networks:
  blog-network:
    driver: bridge

volumes:
  mysql_data:
\`\`\`

环境变量文件 \`.env\`（不提交到 Git）：

\`\`\`bash
MYSQL_ROOT_PASSWORD=your_root_password
MYSQL_USER=blog_user
MYSQL_PASSWORD=your_password
\`\`\`

## 常用 Docker 命令

\`\`\`bash
# 构建并启动所有服务
docker compose up -d --build

# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f backend
docker compose logs -f --tail=100 frontend

# 进入容器
docker compose exec backend sh
docker compose exec mysql mysql -u root -p

# 停止服务
docker compose down

# 停止并删除数据卷（慎用！会删除数据库数据）
docker compose down -v

# 重启单个服务
docker compose restart backend

# 查看镜像大小
docker images | grep blog
\`\`\`

## 镜像优化技巧

### 1. 使用 .dockerignore

\`\`\`
# .dockerignore
node_modules/
dist/
.git/
*.log
target/
\`\`\`

### 2. 合理利用层缓存

\`\`\`dockerfile
# ✅ 先复制 package.json，再安装依赖
# 只有 package.json 变化时才重新安装
COPY package*.json ./
RUN npm ci
COPY . .

# ❌ 先复制所有文件，任何文件变化都会重新安装依赖
COPY . .
RUN npm ci
\`\`\`

### 3. 使用 Alpine 基础镜像

\`\`\`dockerfile
# ❌ 完整镜像（~900MB）
FROM node:18

# ✅ Alpine 镜像（~50MB）
FROM node:18-alpine
\`\`\`

## 生产环境注意事项

1. **不要在容器中存储重要数据**，使用 Volume 持久化
2. **使用非 root 用户**运行应用，提高安全性
3. **设置资源限制**，防止单个容器耗尽资源：

\`\`\`yaml
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 512M
        reservations:
          memory: 256M
\`\`\`

4. **配置健康检查**，确保服务真正可用后再接收流量
5. **使用 secrets 管理敏感信息**，不要在 Dockerfile 中硬编码密码

## 总结

通过本文，我们完成了：

- ✅ Spring Boot 多阶段构建 Dockerfile
- ✅ Vue + Nginx 前端 Dockerfile
- ✅ docker-compose 多服务编排
- ✅ MySQL 数据持久化配置
- ✅ 服务间网络通信
- ✅ 环境变量管理
- ✅ 镜像优化技巧

容器化部署让应用的交付和运维变得更加标准化和可重复，是现代 DevOps 实践的重要基础。
`
