// 文章 3：MySQL 索引优化与查询性能调优实战
export var content3 = `# MySQL 索引优化与查询性能调优实战

## 前言

数据库性能往往是系统瓶颈所在。本文从 B+ 树索引原理出发，结合真实的慢查询案例，系统讲解 MySQL 索引优化的核心技巧。

> **测试环境**：MySQL 8.0.32，数据量约 500 万条

## B+ 树索引原理

MySQL InnoDB 使用 **B+ 树**作为索引结构，理解它是优化的基础。

B+ 树的特点：
- 所有数据存储在**叶子节点**，非叶子节点只存储索引键
- 叶子节点通过**双向链表**连接，支持高效范围查询
- 树高通常只有 **3-4 层**，即使千万级数据也只需 3-4 次 IO

\`\`\`
非叶子节点（索引）
       [30 | 60]
      /    |    \\
   [10|20] [40|50] [70|80]   ← 叶子节点（数据）
     ↔       ↔       ↔       ← 双向链表
\`\`\`

## 索引类型

### 主键索引（聚簇索引）

InnoDB 中，主键索引的叶子节点存储**完整行数据**：

\`\`\`sql
-- 主键查询，直接定位到数据行
SELECT * FROM article WHERE id = 100;
\`\`\`

### 二级索引（非聚簇索引）

二级索引的叶子节点存储**主键值**，查询时需要**回表**：

\`\`\`sql
-- 在 title 上建立索引
CREATE INDEX idx_title ON article(title);

-- 查询过程：先查 idx_title 找到主键，再回表查完整数据
SELECT * FROM article WHERE title = 'MySQL 优化';
\`\`\`

### 覆盖索引

如果查询的字段都在索引中，无需回表，称为**覆盖索引**：

\`\`\`sql
-- 建立联合索引
CREATE INDEX idx_status_time ON article(status, create_time);

-- 只查 id 和 create_time，索引已覆盖，无需回表
SELECT id, create_time FROM article WHERE status = 1;
\`\`\`

## EXPLAIN 分析工具

\`EXPLAIN\` 是分析查询计划的核心工具：

\`\`\`sql
EXPLAIN SELECT * FROM article WHERE category_id = 1 AND status = 1;
\`\`\`

关键字段说明：

| 字段 | 说明 |
|------|------|
| \`type\` | 访问类型，从好到差：\`const > ref > range > index > ALL\` |
| \`key\` | 实际使用的索引 |
| \`rows\` | 预估扫描行数，越小越好 |
| \`Extra\` | 额外信息，\`Using index\` 表示覆盖索引 |

**type 类型详解：**

- \`const\`：主键或唯一索引等值查询，最快
- \`ref\`：非唯一索引等值查询
- \`range\`：索引范围查询（\`BETWEEN\`、\`>\`、\`<\`）
- \`index\`：全索引扫描（比全表扫描好，但仍需优化）
- \`ALL\`：全表扫描，需要立即优化

## 联合索引与最左前缀原则

联合索引遵循**最左前缀原则**：

\`\`\`sql
-- 建立联合索引 (a, b, c)
CREATE INDEX idx_abc ON t(a, b, c);

-- ✅ 能用到索引
SELECT * FROM t WHERE a = 1;
SELECT * FROM t WHERE a = 1 AND b = 2;
SELECT * FROM t WHERE a = 1 AND b = 2 AND c = 3;
SELECT * FROM t WHERE a = 1 AND c = 3;  -- 只用到 a 部分

-- ❌ 不能用到索引
SELECT * FROM t WHERE b = 2;
SELECT * FROM t WHERE c = 3;
SELECT * FROM t WHERE b = 2 AND c = 3;
\`\`\`

## 索引失效场景

以下情况会导致索引失效，需要特别注意：

\`\`\`sql
-- ❌ 对索引列使用函数
SELECT * FROM article WHERE YEAR(create_time) = 2024;
-- ✅ 改为范围查询
SELECT * FROM article WHERE create_time >= '2024-01-01' AND create_time < '2025-01-01';

-- ❌ 隐式类型转换（id 是 INT，传了字符串）
SELECT * FROM article WHERE id = '100';
-- ✅ 类型匹配
SELECT * FROM article WHERE id = 100;

-- ❌ LIKE 以通配符开头
SELECT * FROM article WHERE title LIKE '%Vue%';
-- ✅ 前缀匹配可以用索引
SELECT * FROM article WHERE title LIKE 'Vue%';

-- ❌ OR 条件中有非索引列
SELECT * FROM article WHERE id = 1 OR content = 'xxx';
\`\`\`

## 真实慢查询优化案例

### 案例一：分页查询优化

**问题**：文章列表第 1000 页查询耗时 3 秒

\`\`\`sql
-- 慢查询（OFFSET 很大时性能极差）
SELECT * FROM article ORDER BY create_time DESC LIMIT 10000, 10;
\`\`\`

**原因**：MySQL 需要扫描并丢弃前 10000 条记录。

**优化方案**：使用游标分页（记录上次最后一条的 ID）

\`\`\`sql
-- 优化后（利用主键索引，性能稳定）
SELECT * FROM article
WHERE id < 上次最后一条的ID
ORDER BY id DESC
LIMIT 10;
\`\`\`

或者使用子查询先定位 ID：

\`\`\`sql
SELECT a.* FROM article a
INNER JOIN (
    SELECT id FROM article ORDER BY create_time DESC LIMIT 10000, 10
) t ON a.id = t.id;
\`\`\`

**效果**：从 3s 降至 50ms。

### 案例二：联合索引设计

**问题**：文章列表按分类+状态+时间筛选，查询慢

\`\`\`sql
-- 原始查询
SELECT * FROM article
WHERE category_id = 1 AND status = 1
ORDER BY create_time DESC
LIMIT 10;
\`\`\`

**分析**：\`EXPLAIN\` 显示 \`type=ALL\`，全表扫描。

**优化**：建立联合索引，注意字段顺序（区分度高的放前面）

\`\`\`sql
-- 建立联合索引
CREATE INDEX idx_cat_status_time ON article(category_id, status, create_time);

-- 查询后 EXPLAIN 显示 type=range，Using index
\`\`\`

**效果**：从 800ms 降至 5ms。

## 索引设计原则

1. **选择区分度高的列**：区分度 = 不重复值数量 / 总行数，越接近 1 越好
2. **联合索引字段顺序**：等值查询字段放前面，范围查询字段放后面
3. **避免过多索引**：每个索引都会增加写操作开销，一般不超过 5 个
4. **前缀索引**：对长字符串列，可以只索引前 N 个字符
5. **定期分析**：使用 \`ANALYZE TABLE\` 更新统计信息

\`\`\`sql
-- 查看表的索引使用情况
SELECT * FROM information_schema.INDEX_STATISTICS
WHERE TABLE_NAME = 'article';

-- 查找未使用的索引
SELECT * FROM sys.schema_unused_indexes;
\`\`\`

## 总结

MySQL 索引优化的核心要点：

- ✅ 理解 B+ 树结构，知道为什么索引能加速查询
- ✅ 善用 \`EXPLAIN\` 分析查询计划
- ✅ 遵循最左前缀原则设计联合索引
- ✅ 避免索引失效的常见陷阱
- ✅ 大分页用游标分页替代 OFFSET
- ✅ 覆盖索引减少回表次数

索引优化没有银弹，需要结合具体业务场景和数据分布来设计。
`
