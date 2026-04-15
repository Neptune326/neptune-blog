// 文章 4：TypeScript 高级类型体操实战
export var content4 = `# TypeScript 高级类型体操实战

## 前言

TypeScript 的类型系统极其强大，但很多开发者只用到了冰山一角。本文将深入探讨条件类型、映射类型、模板字面量类型等高级特性，通过实际案例让你真正掌握"类型体操"的精髓。

> **前置知识**：需要了解 TypeScript 基础类型、泛型、接口等基本概念。

## 条件类型

条件类型的语法类似三元表达式：\`T extends U ? X : Y\`

\`\`\`typescript
// 基础条件类型
type IsString<T> = T extends string ? true : false

type A = IsString<string>   // true
type B = IsString<number>   // false

// 实用案例：提取 Promise 的返回类型
type Awaited<T> = T extends Promise<infer R> ? R : T

type C = Awaited<Promise<string>>   // string
type D = Awaited<number>            // number
\`\`\`

### infer 关键字

\`infer\` 用于在条件类型中推断类型变量：

\`\`\`typescript
// 提取函数返回类型
type ReturnType<T> = T extends (...args: any[]) => infer R ? R : never

function greet(name: string): string {
  return 'Hello, ' + name
}

type GreetReturn = ReturnType<typeof greet>  // string

// 提取函数参数类型
type Parameters<T> = T extends (...args: infer P) => any ? P : never

type GreetParams = Parameters<typeof greet>  // [name: string]

// 提取数组元素类型
type ElementType<T> = T extends (infer E)[] ? E : never

type E = ElementType<string[]>  // string
type F = ElementType<[1, 2, 3]>  // 1 | 2 | 3
\`\`\`

## 映射类型

映射类型允许基于已有类型创建新类型：

\`\`\`typescript
// 将所有属性变为可选
type Partial<T> = {
  [K in keyof T]?: T[K]
}

// 将所有属性变为只读
type Readonly<T> = {
  readonly [K in keyof T]: T[K]
}

// 将所有属性变为必填
type Required<T> = {
  [K in keyof T]-?: T[K]  // -? 移除可选修饰符
}

// 实用案例：深度可选
type DeepPartial<T> = {
  [K in keyof T]?: T[K] extends object ? DeepPartial<T[K]> : T[K]
}

interface User {
  name: string
  address: {
    city: string
    street: string
  }
}

type PartialUser = DeepPartial<User>
// { name?: string; address?: { city?: string; street?: string } }
\`\`\`

### 键重映射

TypeScript 4.1+ 支持通过 \`as\` 子句重映射键：

\`\`\`typescript
// 将所有键转为 getter 方法名
type Getters<T> = {
  [K in keyof T as \`get\${Capitalize<string & K>}\`]: () => T[K]
}

interface Person {
  name: string
  age: number
}

type PersonGetters = Getters<Person>
// { getName: () => string; getAge: () => number }

// 过滤特定类型的键
type FilterByType<T, U> = {
  [K in keyof T as T[K] extends U ? K : never]: T[K]
}

interface Mixed {
  name: string
  age: number
  active: boolean
  score: number
}

type StringKeys = FilterByType<Mixed, string>   // { name: string }
type NumberKeys = FilterByType<Mixed, number>   // { age: number; score: number }
\`\`\`

## 模板字面量类型

TypeScript 4.1 引入了模板字面量类型，可以对字符串类型进行操作：

\`\`\`typescript
type EventName = 'click' | 'focus' | 'blur'

// 生成事件处理函数名
type EventHandler = \`on\${Capitalize<EventName>}\`
// 'onClick' | 'onFocus' | 'onBlur'

// 实用案例：CSS 属性值类型
type CSSUnit = 'px' | 'em' | 'rem' | '%'
type CSSValue = \`\${number}\${CSSUnit}\`
// '10px' | '1.5em' | '100%' 等

// 路由参数提取
type ExtractRouteParams<T extends string> =
  T extends \`\${infer _Start}:\${infer Param}/\${infer Rest}\`
    ? Param | ExtractRouteParams<Rest>
    : T extends \`\${infer _Start}:\${infer Param}\`
    ? Param
    : never

type Params = ExtractRouteParams<'/user/:id/post/:postId'>
// 'id' | 'postId'
\`\`\`

## 工具类型实战

### Pick 和 Omit

\`\`\`typescript
interface Article {
  id: number
  title: string
  content: string
  summary: string
  createTime: string
  viewCount: number
}

// 只取部分字段（列表页不需要 content）
type ArticleListItem = Pick<Article, 'id' | 'title' | 'summary' | 'createTime' | 'viewCount'>

// 排除部分字段
type ArticleWithoutContent = Omit<Article, 'content'>
\`\`\`

### Record 类型

\`\`\`typescript
// 创建键值对类型
type StatusMap = Record<'pending' | 'approved' | 'rejected', string>
// { pending: string; approved: string; rejected: string }

const statusLabels: StatusMap = {
  pending: '待审核',
  approved: '已通过',
  rejected: '已拒绝'
}
\`\`\`

### 实用工具类型组合

\`\`\`typescript
// API 响应类型
interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 分页响应
interface PageData<T> {
  total: number
  pages: number
  list: T[]
}

type ArticlePageResponse = ApiResponse<PageData<ArticleListItem>>

// 表单类型（所有字段可选，用于编辑）
type ArticleForm = Partial<Omit<Article, 'id' | 'createTime' | 'viewCount'>>
\`\`\`

## 类型守卫

类型守卫帮助 TypeScript 在运行时缩小类型范围：

\`\`\`typescript
// typeof 守卫
function processValue(value: string | number) {
  if (typeof value === 'string') {
    return value.toUpperCase()  // 这里 value 是 string
  }
  return value.toFixed(2)  // 这里 value 是 number
}

// instanceof 守卫
class ApiError extends Error {
  constructor(public code: number, message: string) {
    super(message)
  }
}

function handleError(error: Error | ApiError) {
  if (error instanceof ApiError) {
    console.log('API 错误码:', error.code)  // 这里可以访问 code
  }
}

// 自定义类型守卫
interface Cat { meow(): void }
interface Dog { bark(): void }

function isCat(animal: Cat | Dog): animal is Cat {
  return 'meow' in animal
}

function makeSound(animal: Cat | Dog) {
  if (isCat(animal)) {
    animal.meow()  // TypeScript 知道这里是 Cat
  } else {
    animal.bark()  // TypeScript 知道这里是 Dog
  }
}
\`\`\`

## 实战：类型安全的事件系统

\`\`\`typescript
// 定义事件映射
interface EventMap {
  'article:created': { id: number; title: string }
  'article:deleted': { id: number }
  'comment:submitted': { articleId: number; nickname: string }
}

// 类型安全的事件发射器
class TypedEventEmitter {
  private listeners: Partial<{
    [K in keyof EventMap]: ((data: EventMap[K]) => void)[]
  }> = {}

  on<K extends keyof EventMap>(
    event: K,
    listener: (data: EventMap[K]) => void
  ) {
    if (!this.listeners[event]) {
      this.listeners[event] = []
    }
    this.listeners[event]!.push(listener)
  }

  emit<K extends keyof EventMap>(event: K, data: EventMap[K]) {
    this.listeners[event]?.forEach(fn => fn(data))
  }
}

const emitter = new TypedEventEmitter()

// 完全类型安全，错误的参数会报错
emitter.on('article:created', ({ id, title }) => {
  console.log(\`文章 \${id}: \${title} 已创建\`)
})

emitter.emit('article:created', { id: 1, title: 'Hello World' })
\`\`\`

## 总结

TypeScript 高级类型的核心要点：

- ✅ **条件类型**：\`T extends U ? X : Y\`，配合 \`infer\` 提取类型
- ✅ **映射类型**：遍历对象键，批量转换属性
- ✅ **模板字面量**：字符串类型的组合与变换
- ✅ **工具类型**：\`Partial\`、\`Pick\`、\`Omit\`、\`Record\` 等
- ✅ **类型守卫**：运行时缩小类型范围

类型体操的目的不是炫技，而是让代码更安全、更易维护。适度使用，避免过度复杂化。
`
