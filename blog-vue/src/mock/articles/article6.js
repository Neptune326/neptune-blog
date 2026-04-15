// 文章 6：Git 工作流与团队协作规范
export var content6 = `# Git 工作流与团队协作规范

## 前言

Git 是现代软件开发不可或缺的工具，但仅仅会用 \`git add\`、\`git commit\`、\`git push\` 是远远不够的。本文将介绍主流的 Git 工作流模型、提交规范和代码审查流程，帮助团队建立高效的协作机制。

## 主流工作流模型

### Git Flow

Git Flow 是最经典的工作流，适合有明确版本发布周期的项目：

\`\`\`
main ─────────────────────────────────────────── v1.0 ── v2.0
       ↑                                          ↑
hotfix/xxx                                    release/1.0
                                                  ↑
develop ──────────────────────────────────────────────────────
           ↑              ↑
      feature/login   feature/article
\`\`\`

**分支说明：**

| 分支 | 说明 | 生命周期 |
|------|------|---------|
| \`main\` | 生产环境代码，只接受 release 和 hotfix 合并 | 永久 |
| \`develop\` | 开发主分支，功能分支的集成点 | 永久 |
| \`feature/*\` | 新功能开发 | 临时 |
| \`release/*\` | 发布准备，只做 bug fix | 临时 |
| \`hotfix/*\` | 生产环境紧急修复 | 临时 |

\`\`\`bash
# 开始新功能
git checkout develop
git checkout -b feature/user-auth

# 功能完成后合并回 develop
git checkout develop
git merge --no-ff feature/user-auth
git branch -d feature/user-auth

# 准备发布
git checkout -b release/1.0 develop
# 修复 bug...
git checkout main
git merge --no-ff release/1.0
git tag -a v1.0 -m "Release 1.0"
git checkout develop
git merge --no-ff release/1.0
\`\`\`

### GitHub Flow

GitHub Flow 更简单，适合持续部署的项目：

\`\`\`
main ──────────────────────────────────────────────────────
         ↑              ↑              ↑
    feature/login   fix/bug-123   feature/search
\`\`\`

**核心规则：**
1. \`main\` 分支始终可部署
2. 从 \`main\` 创建功能分支
3. 定期推送到远程，创建 Pull Request
4. 代码审查通过后合并到 \`main\`
5. 合并后立即部署

\`\`\`bash
# 创建功能分支
git checkout -b feature/article-search

# 开发完成，推送并创建 PR
git push origin feature/article-search
# 在 GitHub 上创建 Pull Request

# PR 合并后，删除分支
git branch -d feature/article-search
git push origin --delete feature/article-search
\`\`\`

## Conventional Commits 提交规范

统一的提交信息格式让 Git 历史更清晰，也便于自动生成 CHANGELOG：

\`\`\`
<type>(<scope>): <subject>

[optional body]

[optional footer]
\`\`\`

**type 类型：**

| 类型 | 说明 | 示例 |
|------|------|------|
| \`feat\` | 新功能 | \`feat(auth): 添加 JWT 登录\` |
| \`fix\` | Bug 修复 | \`fix(article): 修复分页计算错误\` |
| \`docs\` | 文档更新 | \`docs: 更新 README 启动说明\` |
| \`style\` | 代码格式 | \`style: 统一缩进为 2 空格\` |
| \`refactor\` | 重构 | \`refactor(service): 提取公共查询逻辑\` |
| \`test\` | 测试 | \`test: 添加评论状态流转测试\` |
| \`chore\` | 构建/工具 | \`chore: 升级 Spring Boot 到 3.2.5\` |
| \`perf\` | 性能优化 | \`perf(query): 添加文章列表索引\` |
| \`revert\` | 回滚 | \`revert: feat(auth): 添加 JWT 登录\` |

**完整示例：**

\`\`\`bash
# 简单提交
git commit -m "feat(article): 新增文章搜索功能"

# 带 body 的提交（破坏性变更）
git commit -m "feat(api)!: 修改分页参数名称

将 page 改为 pageNum，pageSize 保持不变。

BREAKING CHANGE: 前端需要同步修改请求参数名"

# 关联 Issue
git commit -m "fix(comment): 修复评论提交后状态不刷新的问题

Closes #42"
\`\`\`

## 代码审查（Code Review）最佳实践

### 提交 PR 的规范

\`\`\`markdown
## 变更说明
简要描述本次 PR 做了什么

## 变更类型
- [x] 新功能
- [ ] Bug 修复
- [ ] 重构
- [ ] 文档更新

## 测试说明
- 已在本地测试通过
- 新增了单元测试

## 截图（如有 UI 变更）
[截图]

## 关联 Issue
Closes #42
\`\`\`

### 审查者注意事项

**应该关注的：**
- 逻辑正确性
- 边界条件处理
- 安全漏洞（SQL 注入、XSS 等）
- 性能问题（N+1 查询、大循环等）
- 代码可读性

**不应该纠结的：**
- 个人风格偏好（应由 linter 统一）
- 微小的性能差异

## 常用 Git 技巧

### 交互式 rebase 整理提交

\`\`\`bash
# 整理最近 3 次提交
git rebase -i HEAD~3

# 在编辑器中：
# pick abc1234 feat: 添加登录功能
# squash def5678 fix: 修复登录 bug    ← 合并到上一个
# squash ghi9012 style: 格式化代码    ← 合并到上一个
\`\`\`

### cherry-pick 选择性合并

\`\`\`bash
# 将某个提交应用到当前分支
git cherry-pick abc1234

# 应用多个提交
git cherry-pick abc1234 def5678

# 应用一个范围的提交
git cherry-pick abc1234..ghi9012
\`\`\`

### stash 临时保存工作

\`\`\`bash
# 保存当前工作（包括未跟踪文件）
git stash push -u -m "WIP: 文章搜索功能"

# 查看 stash 列表
git stash list

# 恢复最近的 stash
git stash pop

# 恢复指定的 stash
git stash apply stash@{2}
\`\`\`

### 撤销操作

\`\`\`bash
# 撤销工作区修改（未 add）
git checkout -- src/App.vue

# 撤销暂存区（已 add，未 commit）
git reset HEAD src/App.vue

# 撤销最近一次 commit（保留修改）
git reset --soft HEAD~1

# 撤销最近一次 commit（丢弃修改，慎用）
git reset --hard HEAD~1

# 已推送到远程的提交，用 revert 而不是 reset
git revert abc1234
\`\`\`

### 查找问题提交

\`\`\`bash
# 二分查找引入 bug 的提交
git bisect start
git bisect bad          # 当前版本有 bug
git bisect good v1.0    # v1.0 没有 bug
# Git 会自动切换到中间的提交，测试后标记 good/bad
git bisect good  # 或 git bisect bad
# 最终找到引入 bug 的提交
git bisect reset  # 结束二分查找
\`\`\`

## .gitignore 配置

\`\`\`gitignore
# Java
target/
*.class
*.jar
*.war

# IDE
.idea/
*.iml
.vscode/
.DS_Store

# Node
node_modules/
dist/
.env.local
.env.production

# 日志
*.log
logs/

# 敏感配置（不提交生产环境配置）
application-prod.yml
.env
\`\`\`

## 总结

良好的 Git 工作流是团队协作的基础：

- ✅ 根据项目特点选择合适的工作流（Git Flow / GitHub Flow）
- ✅ 遵循 Conventional Commits 规范，让提交历史清晰可读
- ✅ 认真对待 Code Review，它是提升代码质量的重要手段
- ✅ 掌握 rebase、cherry-pick、stash 等高级技巧
- ✅ 配置好 .gitignore，避免提交不必要的文件

Git 的核心是协作，规范的目的是让团队更高效，而不是增加负担。
`
