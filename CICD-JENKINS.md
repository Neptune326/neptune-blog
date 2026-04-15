# CI/CD 部署指南 —— GitHub + Jenkins + Linux 服务器

## 一、架构概览

```
开发者本地
    │
    │ git push
    ▼
GitHub 仓库
    │
    │ Webhook 触发
    ▼
Jenkins（可部署在服务器上）
    │
    ├── 拉取代码
    ├── 构建后端（Maven）
    ├── 构建前端（npm build）
    ├── 打包 / 上传
    └── 部署到 Linux 服务器
```

---

## 二、服务器环境准备

以一台 Linux 服务器（Ubuntu 22.04 / CentOS 7）为例。

### 1. 安装 Java 17

```bash
# Ubuntu
sudo apt update
sudo apt install -y openjdk-17-jdk

# CentOS
sudo yum install -y java-17-openjdk-devel

# 验证
java -version
```

### 2. 安装 Maven

```bash
# Ubuntu
sudo apt install -y maven

# 验证
mvn -version
```

### 3. 安装 Node.js 18+

```bash
# 使用 nvm 安装（推荐）
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
source ~/.bashrc
nvm install 18
nvm use 18

# 验证
node -v
npm -v
```

### 4. 安装 MySQL 8

```bash
# Ubuntu
sudo apt install -y mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql

# 初始化数据库
mysql -u root -p < /path/to/blog-springboot/src/main/resources/sql/init.sql
```

### 5. 安装 Jenkins

```bash
# Ubuntu
wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
sudo apt update
sudo apt install -y jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# 访问 Jenkins：http://<服务器IP>:8080
# 获取初始密码
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

---

## 三、Jenkins 配置

### 1. 安装必要插件

在 Jenkins 管理界面 → 插件管理，安装：
- **Git Plugin**：拉取 GitHub 代码
- **Maven Integration Plugin**：构建 Java 项目
- **NodeJS Plugin**：构建前端项目
- **SSH Agent Plugin** 或 **Publish Over SSH**：部署到远程服务器
- **GitHub Integration Plugin**：接收 GitHub Webhook

### 2. 配置 NodeJS 工具

Jenkins → 全局工具配置 → NodeJS → 新增 NodeJS，选择版本 18。

### 3. 配置 SSH 凭据

Jenkins → 凭据管理 → 新增凭据：
- 类型：SSH Username with private key
- ID：`deploy-server`
- 用户名：服务器用户名（如 `ubuntu`）
- 私钥：粘贴本地 `~/.ssh/id_rsa` 内容

---

## 四、创建 Jenkins Pipeline

### 1. 新建 Pipeline 任务

Jenkins 首页 → 新建任务 → 选择 Pipeline。

### 2. 配置 GitHub Webhook 触发

- 任务配置 → 构建触发器 → 勾选 **GitHub hook trigger for GITScm polling**
- 在 GitHub 仓库 → Settings → Webhooks → Add webhook：
  - Payload URL：`http://<Jenkins服务器IP>:8080/github-webhook/`
  - Content type：`application/json`
  - 触发事件：`push`

### 3. Jenkinsfile（放在项目根目录）

在项目根目录创建 `Jenkinsfile`：

```groovy
pipeline {
    agent any

    tools {
        nodejs 'NodeJS-18'
    }

    environment {
        // 服务器部署目录
        DEPLOY_DIR = '/opt/blog'
        // 服务器 SSH 凭据 ID
        SSH_CREDENTIAL = 'deploy-server'
        // 服务器地址
        REMOTE_HOST = '你的服务器IP'
        REMOTE_USER = 'ubuntu'
    }

    stages {

        stage('拉取代码') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/<your-username>/<your-repo>.git'
            }
        }

        stage('构建后端') {
            steps {
                dir('blog-springboot') {
                    sh 'mvn clean package -DskipTests -P prod'
                }
            }
        }

        stage('构建前端') {
            steps {
                dir('blog-vue') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('部署后端') {
            steps {
                sshagent(credentials: [SSH_CREDENTIAL]) {
                    sh """
                        # 上传 jar 包
                        scp -o StrictHostKeyChecking=no \\
                            blog-springboot/target/blog-springboot-1.0.0.jar \\
                            ${REMOTE_USER}@${REMOTE_HOST}:${DEPLOY_DIR}/

                        # 重启后端服务
                        ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} '
                            # 停止旧进程
                            pid=\$(lsof -ti:8080)
                            if [ -n "\$pid" ]; then
                                kill -9 \$pid
                                echo "已停止旧进程 PID: \$pid"
                            fi

                            # 启动新进程（正式环境配置）
                            nohup java -jar ${DEPLOY_DIR}/blog-springboot-1.0.0.jar \\
                                --spring.profiles.active=prod \\
                                > ${DEPLOY_DIR}/logs/app.log 2>&1 &

                            echo "后端服务已启动"
                        '
                    """
                }
            }
        }

        stage('部署前端') {
            steps {
                sshagent(credentials: [SSH_CREDENTIAL]) {
                    sh """
                        # 上传前端构建产物
                        scp -o StrictHostKeyChecking=no -r \\
                            blog-vue/dist/* \\
                            ${REMOTE_USER}@${REMOTE_HOST}:${DEPLOY_DIR}/frontend/

                        echo "前端文件已部署"
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ 部署成功！'
        }
        failure {
            echo '❌ 部署失败，请检查日志。'
        }
    }
}
```

---

## 五、服务器目录结构

```
/opt/blog/
├── blog-springboot-1.0.0.jar   # 后端 jar 包
├── frontend/                    # 前端静态文件（Nginx 指向此目录）
└── logs/
    └── app.log                  # 后端运行日志
```

初始化目录：

```bash
sudo mkdir -p /opt/blog/logs /opt/blog/frontend
sudo chown -R ubuntu:ubuntu /opt/blog
```

---

## 六、配置 Nginx（前端 + 反向代理）

```bash
sudo apt install -y nginx
```

创建配置文件 `/etc/nginx/sites-available/blog`：

```nginx
server {
    listen 80;
    server_name 你的域名或IP;

    # 前端静态文件
    root /opt/blog/frontend;
    index index.html;

    # 前端路由（Vue Router history 模式）
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

启用配置：

```bash
sudo ln -s /etc/nginx/sites-available/blog /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

## 七、Maven 多环境打包

在 `blog-springboot/pom.xml` 中添加 profiles 配置（如果尚未添加）：

```xml
<profiles>
    <profile>
        <id>dev</id>
        <activation><activeByDefault>true</activeByDefault></activation>
        <properties><spring.profiles.active>dev</spring.profiles.active></properties>
    </profile>
    <profile>
        <id>test</id>
        <properties><spring.profiles.active>test</spring.profiles.active></properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties><spring.profiles.active>prod</spring.profiles.active></properties>
    </profile>
</profiles>
```

打包命令：

```bash
# 打包正式环境
mvn clean package -DskipTests -P prod

# 打包测试环境
mvn clean package -DskipTests -P test
```
