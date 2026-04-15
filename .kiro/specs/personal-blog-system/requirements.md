# 需求文档

## 简介

本文档描述个人博客系统的功能需求。该系统面向个人博主，提供文章发布与管理、分类标签管理、评论互动等核心功能。

系统采用前后端分离架构，由两个项目组成：
- **后端（blog-springboot）**：单个 Spring Boot 应用，同时提供前台 API（路径前缀 `/api`）和后台 API（路径前缀 `/api/admin`），共用同一个 MySQL 数据库
- **前端（blog-vue）**：单个 Vue 单页应用，通过 Vue Router 路由前缀区分前台和后台界面：
  - 默认路由 `/` 进入前台博客首页（面向访客）
  - `/admin` 路由前缀进入后台管理界面（需要登录，面向管理员）

技术栈：
- 后端：Spring Boot 3.x、MyBatis-Plus、MySQL、Sa-Token（单机部署）
- 前端：Vue、Vuetify、Tailwind CSS（ES5 规范）

---

## 词汇表

- **系统（System）**：整个个人博客系统
- **管理员（Admin）**：拥有后台管理权限的用户，通过 Sa-Token 认证
- **访客（Visitor）**：未登录的普通浏览者
- **文章（Article）**：博客中发布的内容单元，包含标题、正文、分类、标签等
- **分类（Category）**：对文章进行归类的层级结构，每篇文章属于一个分类
- **标签（Tag）**：对文章进行多维度标注的关键词，一篇文章可拥有多个标签
- **评论（Comment）**：访客或管理员对文章发表的回复内容
- **后台（Admin_Panel）**：管理员专用的管理界面
- **前台（Frontend）**：面向访客的博客展示界面
- **认证服务（Auth_Service）**：基于 Sa-Token 实现的登录认证与会话管理模块
- **文章服务（Article_Service）**：负责文章 CRUD 及状态管理的后端模块
- **分类服务（Category_Service）**：负责分类 CRUD 的后端模块
- **标签服务（Tag_Service）**：负责标签 CRUD 的后端模块
- **评论服务（Comment_Service）**：负责评论提交、审核、删除的后端模块
- **博客后端（Blog_Backend）**：blog-springboot Spring Boot 应用，同时提供前台 API（路径前缀 `/api`）和后台 API（路径前缀 `/api/admin`），共用同一个 MySQL 数据库
- **博客前端（Blog_Vue）**：blog-vue Vue 单页应用，通过路由前缀区分前台和后台界面
- **前台路由（Frontend_Route）**：以 `/` 为根路径的前台博客页面路由
- **后台路由（Admin_Route）**：以 `/admin` 为前缀的后台管理页面路由

---

## 需求

### 需求 1：用户认证

**用户故事：** 作为管理员，我希望通过用户名和密码登录后台，以便安全地管理博客内容。

#### 验收标准

1. THE Auth_Service SHALL 提供管理员登录接口，接受用户名和密码参数
2. WHEN 管理员提交正确的用户名和密码，THE Auth_Service SHALL 颁发 Sa-Token 令牌并返回给客户端
3. IF 管理员提交的用户名或密码错误，THEN THE Auth_Service SHALL 返回认证失败错误信息，且不颁发令牌
4. WHEN 管理员成功登录，THE Auth_Service SHALL 在服务端创建会话，有效期为 7 天
5. WHEN 管理员请求登出，THE Auth_Service SHALL 销毁当前会话并使令牌失效
6. WHILE 管理员未登录，THE Admin_Panel SHALL 拒绝访问所有管理接口并重定向至登录页面
7. IF 令牌已过期，THEN THE Auth_Service SHALL 返回 401 状态码，提示重新登录

---

### 需求 2：文章管理

**用户故事：** 作为管理员，我希望能够发布、编辑、删除和管理文章，以便维护博客内容。

#### 验收标准

1. THE Article_Service SHALL 提供创建文章接口，接受标题、正文（Markdown 格式）、分类 ID、标签 ID 列表、封面图片 URL、摘要、发布状态（草稿/已发布）参数
2. WHEN 管理员创建文章时未填写标题或正文，THE Article_Service SHALL 返回参数校验错误，拒绝创建
3. WHEN 管理员成功创建文章，THE Article_Service SHALL 记录文章创建时间并持久化至数据库
4. THE Article_Service SHALL 提供编辑文章接口，允许管理员修改文章的标题、正文、分类、标签、封面、摘要和发布状态
5. WHEN 管理员更新文章，THE Article_Service SHALL 记录最后修改时间
6. WHEN 管理员删除文章，THE Article_Service SHALL 将文章标记为已删除状态（软删除），不物理删除数据库记录
7. THE Article_Service SHALL 提供文章列表查询接口，支持按分类、标签、发布状态、关键词进行筛选
8. THE Article_Service SHALL 提供文章详情查询接口，返回文章完整内容及关联的分类和标签信息
9. WHEN 访客查询文章列表，THE Article_Service SHALL 仅返回发布状态为"已发布"的文章
10. WHEN 管理员查询文章列表，THE Article_Service SHALL 返回所有状态（草稿、已发布）的文章
11. THE Article_Service SHALL 支持文章列表分页查询，每页默认返回 10 条记录

---

### 需求 3：分类管理

**用户故事：** 作为管理员，我希望能够创建和管理文章分类，以便对博客内容进行有序归类。

#### 验收标准

1. THE Category_Service SHALL 提供创建分类接口，接受分类名称和描述参数
2. IF 管理员创建分类时提交的分类名称已存在，THEN THE Category_Service SHALL 返回重复名称错误，拒绝创建
3. THE Category_Service SHALL 提供编辑分类接口，允许管理员修改分类名称和描述
4. WHEN 管理员删除分类时，该分类下存在文章，THE Category_Service SHALL 拒绝删除并返回提示信息
5. WHEN 管理员删除不含文章的分类，THE Category_Service SHALL 将该分类从数据库中删除
6. THE Category_Service SHALL 提供分类列表查询接口，返回所有分类及各分类下的文章数量
7. THE Frontend SHALL 在导航栏展示所有分类，供访客按分类浏览文章

---

### 需求 4：标签管理

**用户故事：** 作为管理员，我希望能够创建和管理文章标签，以便为文章添加多维度的关键词标注。

#### 验收标准

1. THE Tag_Service SHALL 提供创建标签接口，接受标签名称参数
2. IF 管理员创建标签时提交的标签名称已存在，THEN THE Tag_Service SHALL 返回重复名称错误，拒绝创建
3. THE Tag_Service SHALL 提供编辑标签接口，允许管理员修改标签名称
4. WHEN 管理员删除标签，THE Tag_Service SHALL 同时解除该标签与所有文章的关联关系，再删除标签记录
5. THE Tag_Service SHALL 提供标签列表查询接口，返回所有标签及各标签下的文章数量
6. THE Frontend SHALL 在文章详情页展示该文章的所有标签，访客点击标签后可查看该标签下的所有文章

---

### 需求 5：评论功能

**用户故事：** 作为访客，我希望能够对文章发表评论，以便与博主进行互动交流。

#### 验收标准

1. WHEN 访客在已发布文章下提交评论，THE Comment_Service SHALL 接受昵称、邮箱（可选）、评论内容参数
2. IF 访客提交评论时未填写昵称或评论内容，THEN THE Comment_Service SHALL 返回参数校验错误，拒绝提交
3. WHEN 访客成功提交评论，THE Comment_Service SHALL 将评论状态设为"待审核"，不立即在前台展示
4. THE Frontend SHALL 仅展示审核状态为"已通过"的评论
5. WHEN 管理员审核通过评论，THE Comment_Service SHALL 将评论状态更新为"已通过"，评论随即在前台可见
6. WHEN 管理员拒绝评论，THE Comment_Service SHALL 将评论状态更新为"已拒绝"
7. WHEN 管理员删除评论，THE Comment_Service SHALL 将评论从数据库中物理删除
8. THE Comment_Service SHALL 提供评论列表查询接口，支持按文章 ID 和审核状态筛选
9. THE Frontend SHALL 在文章详情页展示该文章已通过审核的评论列表，按评论时间升序排列

---

### 需求 6：前台展示

**用户故事：** 作为访客，我希望能够浏览博客的文章列表和文章详情，以便阅读博客内容。

#### 验收标准

1. THE Frontend SHALL 提供博客首页，展示最新发布文章列表，包含文章标题、摘要、封面图、发布时间、分类和标签
2. THE Frontend SHALL 支持文章列表分页展示，每页显示 10 篇文章
3. WHEN 访客点击文章标题或封面，THE Frontend SHALL 跳转至文章详情页，展示文章完整内容（Markdown 渲染后的 HTML）
4. WHEN 访客访问文章详情页，THE Article_Service SHALL 将该文章的阅读次数加 1
5. THE Frontend SHALL 提供搜索功能，WHEN 访客输入关键词并提交，THE Frontend SHALL 展示标题或正文包含该关键词的文章列表
6. THE Frontend SHALL 提供分类页面，展示指定分类下的所有已发布文章
7. THE Frontend SHALL 提供标签页面，展示指定标签下的所有已发布文章
8. THE Frontend SHALL 提供归档页面，按年月展示所有已发布文章的时间线
9. THE Frontend SHALL 在文章详情页底部展示上一篇和下一篇文章的导航链接

---

### 需求 7：后台管理界面

**用户故事：** 作为管理员，我希望拥有一个统一的后台管理界面，以便集中管理博客的所有内容。

#### 验收标准

1. THE Admin_Panel SHALL 提供仪表盘页面，展示文章总数、分类总数、标签总数、评论总数（待审核数）等统计数据
2. THE Admin_Panel SHALL 提供文章管理页面，支持文章的新建、编辑、删除、发布/取消发布操作
3. THE Admin_Panel SHALL 提供分类管理页面，支持分类的新建、编辑、删除操作
4. THE Admin_Panel SHALL 提供标签管理页面，支持标签的新建、编辑、删除操作
5. THE Admin_Panel SHALL 提供评论管理页面，展示所有评论列表，支持按审核状态筛选，并提供审核通过、拒绝、删除操作
6. WHILE 管理员在后台管理界面操作，THE Admin_Panel SHALL 在页面顶部或侧边栏持续展示导航菜单
7. THE Admin_Panel SHALL 提供文章编辑器，支持 Markdown 格式输入及实时预览

---

### 需求 8：数据持久化与一致性

**用户故事：** 作为系统，我希望所有数据能够可靠地持久化，以便保证博客内容不丢失。

#### 验收标准

1. THE System SHALL 使用 MySQL 数据库持久化所有业务数据，包括文章、分类、标签、评论和管理员账户
2. WHEN 文章与标签建立关联，THE System SHALL 在关联表中记录文章 ID 和标签 ID 的对应关系
3. THE System SHALL 对所有数据库写操作使用事务，IF 事务中任意操作失败，THEN THE System SHALL 回滚全部操作并返回错误信息
4. THE Article_Service SHALL 在文章列表查询时支持按创建时间降序排列（默认）和按阅读次数降序排列

---

### 需求 9：后台 API 统一规范

**用户故事：** 作为开发者，我希望 blog-springboot 的后台 API 具备统一的接口规范，以便前后端协作高效、系统行为可预期。

#### 验收标准

1. THE Blog_Backend SHALL 对所有后台接口返回统一的 JSON 响应结构，包含 `code`（业务状态码）、`message`（描述信息）、`data`（业务数据）三个字段
2. WHEN Blog_Backend 处理后台请求成功，THE Blog_Backend SHALL 返回 `code` 为 200、`message` 为 "success"、`data` 为实际业务数据的响应体
3. THE Blog_Backend SHALL 配置全局异常拦截器，对业务异常、参数校验异常、系统异常分类捕获，并返回统一 JSON 格式的错误响应
4. IF 请求触发业务异常，THEN THE Blog_Backend SHALL 返回对应业务错误码和描述信息，HTTP 状态码为 200
5. IF 请求触发参数校验异常，THEN THE Blog_Backend SHALL 返回 `code` 为 400、`message` 包含具体字段校验失败原因的响应体
6. IF 请求触发系统未捕获异常，THEN THE Blog_Backend SHALL 返回 `code` 为 500、`message` 为通用错误提示的响应体，并记录完整异常堆栈至日志
7. THE Blog_Backend SHALL 使用 Spring Validation 注解对所有后台请求参数进行校验，校验失败时返回第 5 条规定的统一格式
8. THE Blog_Backend SHALL 通过日志记录每次后台请求的请求路径、HTTP 方法、请求参数、响应耗时
9. WHEN Blog_Backend 捕获异常，THE Blog_Backend SHALL 在日志中记录异常类型、异常信息及完整堆栈
10. THE Blog_Backend SHALL 配置 CORS，允许指定的前端域名跨域访问所有 `/api/admin/**` 路径
11. THE Blog_Backend SHALL 将所有后台 API 路径统一以 `/api/admin` 为前缀
12. THE Blog_Backend SHALL 对所有后台分页查询接口统一使用 `pageNum`（页码，从 1 开始）和 `pageSize`（每页条数）作为请求参数
13. WHEN Blog_Backend 返回后台分页数据，THE Blog_Backend SHALL 在 `data` 字段中统一包含 `total`（总记录数）、`pages`（总页数）、`list`（当前页数据列表）字段

---

### 需求 10：前台 API 统一规范

**用户故事：** 作为开发者，我希望 blog-springboot 的前台 API 具备统一的接口规范，以便访客端前端与后端协作一致。

#### 验收标准

1. THE Blog_Backend SHALL 对所有前台接口返回与后台 API 相同的统一 JSON 响应结构，包含 `code`、`message`、`data` 三个字段
2. WHEN Blog_Backend 处理前台请求成功，THE Blog_Backend SHALL 返回 `code` 为 200、`message` 为 "success"、`data` 为实际业务数据的响应体
3. THE Blog_Backend SHALL 对前台接口配置全局异常拦截，对资源不存在（404）、参数错误、系统异常分类捕获，并返回统一 JSON 格式的错误响应
4. IF 访客请求的资源不存在，THEN THE Blog_Backend SHALL 返回 `code` 为 404、`message` 为资源不存在提示的响应体
5. IF 前台请求触发参数错误，THEN THE Blog_Backend SHALL 返回 `code` 为 400、`message` 包含具体错误原因的响应体
6. IF 前台请求触发系统未捕获异常，THEN THE Blog_Backend SHALL 返回 `code` 为 500、`message` 为通用错误提示的响应体，并记录异常至日志
7. THE Blog_Backend SHALL 使用 Spring Validation 注解对所有前台请求参数进行校验，校验失败时返回第 5 条规定的统一格式
8. THE Blog_Backend SHALL 通过日志记录每次前台访问请求的请求路径、HTTP 方法、响应耗时
9. THE Blog_Backend SHALL 配置 CORS，允许指定的前端域名跨域访问所有 `/api/**` 路径
10. THE Blog_Backend SHALL 将所有前台 API 路径统一以 `/api` 为前缀
11. THE Blog_Backend SHALL 对所有前台分页查询接口统一使用 `pageNum` 和 `pageSize` 作为请求参数
12. WHEN Blog_Backend 返回前台分页数据，THE Blog_Backend SHALL 在 `data` 字段中统一包含 `total`、`pages`、`list` 字段，与后台 API 分页响应格式保持一致

---

### 需求 11：前端工程规范

**用户故事：** 作为前端开发者，我希望 blog-vue 具备统一的工程规范，以便代码风格一致、路由清晰、错误处理可靠。

#### 验收标准

1. THE Blog_Vue SHALL 配置默认路由 `/` 指向前台博客首页
2. THE Blog_Vue SHALL 将所有后台管理页面路由统一以 `/admin` 为前缀
3. WHEN 访客访问 `/admin` 前缀路由且未登录，THE Blog_Vue SHALL 重定向至 `/admin/login` 登录页
4. WHEN 管理员成功登录，THE Blog_Vue SHALL 跳转至 `/admin/dashboard` 仪表盘页面
5. THE Blog_Vue SHALL 基于 axios 封装统一的 HTTP 请求模块，根据请求路径自动判断是否注入 Token：`/api/admin` 路径自动注入 Token，`/api` 路径不注入 Token
6. WHEN 接口返回错误响应（`code` 非 200），THE Blog_Vue SHALL 通过 toast 组件统一展示错误提示信息
7. IF 接口返回 `code` 为 401，THEN THE Blog_Vue SHALL 自动清除本地登录态并跳转至 `/admin/login` 登录页面
8. WHILE 请求正在进行中，THE Blog_Vue SHALL 展示统一的加载状态（Loading）
9. WHEN 管理员成功登录，THE Blog_Vue SHALL 将登录态（Token）持久化至本地存储，并在后续后台请求中自动携带
