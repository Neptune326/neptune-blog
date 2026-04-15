# Git 使用指南 —— 与 GitHub 仓库建立关联

## 一、前提条件

- 已安装 Git（`git --version` 验证）
- 已注册 GitHub 账号
- 已在 GitHub 上创建一个新的空仓库（不要勾选初始化 README）

---

## 二、初次关联并推送

### 1. 在本地项目根目录初始化 Git

```bash
git init
```

### 2. 添加 .gitignore 文件（推荐）

在项目根目录创建 `.gitignore`，忽略不需要提交的文件：

```
# Java
target/
*.class
*.jar

# Maven
.mvn/
mvnw
mvnw.cmd

# IDE
.idea/
*.iml
.vscode/

# Node
node_modules/
dist/
.env.local

# 日志
*.log
logs/

# 正式环境配置（含敏感信息，不提交）
blog-springboot/src/main/resources/application-prod.yml
```

### 3. 将所有文件添加到暂存区

```bash
git add .
```

### 4. 提交第一次记录

```bash
git commit -m "feat: 初始化个人博客系统项目"
```

### 5. 关联远程 GitHub 仓库

将 `<your-username>` 和 `<your-repo>` 替换为你的 GitHub 用户名和仓库名：

```bash
git remote add origin https://github.com/<your-username>/<your-repo>.git
```

### 6. 推送到 GitHub

```bash
# 首次推送，设置上游分支
git push -u origin main
```

> 如果你的默认分支是 `master`，将 `main` 替换为 `master`。

---

## 三、日常开发流程

```bash
# 查看当前状态
git status

# 查看文件变更内容
git diff

# 添加指定文件到暂存区
git add <文件路径>

# 添加所有变更
git add .

# 提交
git commit -m "feat: 添加文章管理功能"

# 推送到远程
git push
```

---

## 四、分支管理

```bash
# 查看所有分支
git branch -a

# 创建并切换到新分支
git checkout -b feature/article-module

# 切换分支
git checkout main

# 合并分支（先切换到目标分支）
git checkout main
git merge feature/article-module

# 删除本地分支
git branch -d feature/article-module

# 删除远程分支
git push origin --delete feature/article-module
```

---

## 五、同步远程更新

```bash
# 拉取远程最新代码并合并
git pull

# 仅拉取不合并（查看远程变更）
git fetch origin

# 查看远程与本地的差异
git log origin/main..HEAD
```

---

## 六、撤销操作

```bash
# 撤销工作区的修改（未 add）
git checkout -- <文件路径>

# 撤销暂存区（已 add，未 commit）
git reset HEAD <文件路径>

# 撤销最近一次 commit（保留文件修改）
git reset --soft HEAD~1

# 查看提交历史
git log --oneline --graph
```

---

## 七、常用提交信息规范（Conventional Commits）

| 前缀 | 含义 |
|------|------|
| `feat:` | 新功能 |
| `fix:` | Bug 修复 |
| `docs:` | 文档更新 |
| `style:` | 代码格式调整 |
| `refactor:` | 重构 |
| `test:` | 测试相关 |
| `chore:` | 构建/工具配置 |

示例：
```bash
git commit -m "feat: 新增评论审核功能"
git commit -m "fix: 修复分类删除时未检查文章关联的问题"
git commit -m "docs: 更新项目启动说明"
```
