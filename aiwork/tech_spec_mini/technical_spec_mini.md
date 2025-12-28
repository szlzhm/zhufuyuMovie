# zhufuyuMovie 简化技术规范

## 1. 技术栈与运行环境

- **JDK**: 17（LTS，保持与 Java 8 语法基本兼容，可逐步使用新特性）。
- **后端框架**: Spring Boot 单体应用（后续可演进至 Spring Cloud）。
- **数据库**: MySQL 8.0。
- **缓存**: Redis 4.x（Spring Data Redis）。
- **ORM**: Spring Data JPA（默认使用 Hibernate 作为 JPA 实现）。
- **构建工具**: Maven 或 Gradle（二选一即可，按团队习惯）。
- **前端**: React（使用 Node.js 仅做前端开发与打包，不用于后端服务）。

---

## 2. 架构与分层规范

### 2.1 分层结构

项目采用简化的四层结构：

- **Controller 层**
  - 提供 HTTP 接口，负责：入参接收、基础校验、调用 Service。
  - **禁止**编写业务逻辑，只做参数检查与结果封装。

- **Service 层**
  - 负责业务逻辑、事务控制、缓存控制、跨 Repository 编排。
  - 在此层完成 DTO ↔ 实体 的转换。

- **Repository/DAO 层**
  - 封装对 MySQL 的 CRUD 操作。
  - 统一使用 Spring Data JPA 的 Repository 接口或自定义仓储实现。

- **Entity 层**
  - 数据库实体映射类（`@Entity`），与表结构一一对应。

### 2.2 依赖方向

- 严格单向：`Controller -> Service -> Repository -> Entity`。
- **禁止** Controller 直接调用 Repository/Entity。
- **禁止** 逆向依赖和跨层“跳级”访问（例如 Service 调用其他模块的 Controller）。

### 2.3 包结构建议

```text
com.zhufuyu.movie
├── controller        // 控制器
├── service           // 业务接口
│   └── impl          // 业务实现
├── repository        // 持久层（或 dao）
├── entity            // 实体类
├── model
│   ├── request       // Req DTO
│   └── response      // Resp DTO
├── config            // 配置类
├── exception         // 异常定义
└── util              // 工具类
```

---

## 3. API 设计规范（简化版 RPC 风格）

### 3.1 路径风格

统一采用功能导向（RPC 风格）路径，便于未来迁移 Spring Cloud：

- **通用 API 格式**: `/api/{module}/{action}/{resource}/v{version}`
  - 示例：`/api/template/query/list/v1`、`/api/video/create/batch/v1`。
- **后台管理 API 格式**: `/api/{module}/admin/{action}/{resource}/v{version}`
  - 示例：`/api/template/admin/create/one/v1`。

### 3.2 HTTP 方法

- 绝大多数接口：使用 **POST + JSON Body**（包括复杂查询、创建、修改、删除）。
- **禁止使用 PUT 和 DELETE 方法**：删除、修改操作均通过 POST 完成，通过请求体 JSON 中的字段区分操作类型。
- 仅非常简单、无参数的查询（例如健康检查）可使用 GET。

### 3.3 请求与响应模型

- **请求 DTO**
  - 命名以 `Req` 结尾，如 `CreateVideoTaskReq`。
  - 存放于 `model.request` 包中。
  - 使用 Bean Validation 注解（`@NotNull`、`@Size` 等）进行基础校验。
  - 如有复杂业务校验，可在类中提供 `checkArgs()` 方法补充。

- **响应 DTO**
  - 命名以 `Resp` 结尾，如 `VideoTaskDetailResp`。
  - 存放于 `model.response` 包中。
  - Controller **禁止**直接返回实体（Entity），必须返回 Resp。

### 3.4 控制器规范

- Controller 方法：
  - 入参使用 `@RequestBody` + `Req`；
  - 使用 `@Valid` 或调用 `req.checkArgs()` 进行参数校验；
  - 调用 Service，返回 `Resp`。
- 所有对外接口建议添加 Swagger 注解（`@Api`/`@ApiOperation`）。

---

## 4. 数据库设计规范（MySQL 8.0）

### 4.1 设计原则

- **业务逻辑在应用层**：
  - 禁止使用存储过程、触发器、自定义函数实现业务逻辑；
  - 不使用外键约束，关联关系由应用层保证。

### 4.2 命名规范

- 表名：小写 + 下划线，单数名词，例如：`video_task`, `template_config`。
- 字段名：小写 + 下划线，例如：`user_id`, `create_time`。
- 主键：统一使用 `id BIGINT` 自增。
- 索引命名：
  - 主键索引：`pk_{table}`；
  - 唯一索引：`uk_{table}_{column}`；
  - 普通索引：`idx_{table}_{column}`。

### 4.3 字段类型

- 布尔：`tinyint(1)`，字段名以 `is_` 开头（例如 `is_deleted`）。
- 金额/精度数值：`decimal(18,2)` 或按业务指定，禁止使用 `float/double` 处理金额。
- 文本：根据实际情况使用 `varchar(n)`，大文本使用 `text/longtext`。
- 时间：统一使用 `create_time`、`modify_time`（`datetime` 或 `timestamp`）。

### 4.4 删除策略

- 当前项目数据量有限，推荐 **物理删除 + 必要审计记录**。
- 如未来有强审计需求，再评估逻辑删除字段（`is_deleted`）。

### 4.5 SQL 与 Repository 实现

- 推荐统一使用 Spring Data JPA：
  - 简单 CRUD 使用 `JpaRepository` 或 `CrudRepository` 接口；
  - 复杂查询使用 `@Query` + JPQL 或 `Specification` 等。
- 一般约束：
  - 禁用 `SELECT *`，显式列出必要字段；
  - 避免 N+1 查询，合理使用 IN 批量查询；
  - 批量操作控制单次数量（建议单次 IN 条数 ≤ 1000）。

---

## 5. Redis 缓存规范（Redis 4.x）

### 5.1 使用范围

- 用户会话/登录态（如不用 JWT 时）；
- 热点数据缓存：如模板列表、配置；
- 短期任务状态缓存（可结合数据库持久化）。

### 5.2 Key 规范

- 统一 Key 前缀：`{system}:{module}:{biz}:{id}`，例如：
  - `bless:user:session:{userId}`
  - `bless:video:task:{taskId}`

### 5.3 使用方式

- 使用 Spring Data Redis 或 Spring Cache 注解统一访问 Redis。
- 设置合理 TTL：
  - 热点数据：5 ~ 60 分钟；
  - 任务状态：短期（几分钟）；
  - 会话：与登录有效期一致。

---

## 6. Java 代码与命名规范

### 6.1 命名约定

- Controller：`XxxController`（例如 `TemplateController`）。
- Service 接口：`XxxService`（例如 `TemplateService`），实现：`XxxServiceImpl`。
- Repository/Dao：`XxxRepository` 或 `XxxDao`。
- 实体类：`XxxEntity` 或 `XxxDdo`（本项目可统一为 `Entity`）。
- 请求 DTO：`XxxReq`；响应 DTO：`XxxResp`。

### 6.2 代码风格

- 缩进统一 4 空格，不使用 Tab。
- 方法与变量使用 `lowerCamelCase`；类名使用 `UpperCamelCase`；常量使用 `UPPER_SNAKE_CASE`。
- 禁止使用 `System.out.println`，统一使用日志框架。

### 6.3 Spring 使用规范

- 新代码优先使用 **构造函数注入**（`@RequiredArgsConstructor` + `final` 字段）。
- 避免在同一个类中直接调用带 `@Transactional` / `@Async` 注解的方法，必要时抽到独立 Bean。

---

## 7. 日志与异常规范

### 7.1 日志

- 使用 SLF4J + Logback。
- 使用参数化日志：`log.info("User {} created video {}", userId, taskId);`。
- 禁止在日志中输出密码、密钥、Token、身份证号等敏感信息。

### 7.2 异常

- 定义统一业务异常类（例如 `BizException`），包含错误码与错误信息。
- Controller 不直接处理业务异常，统一由 `@ControllerAdvice` + 全局异常处理器转换为标准响应。
- 禁止空 `catch` 块，所有异常捕获都要有处理或向上抛出。

---

## 8. 性能与安全基本要求

### 8.1 性能

- 所有列表接口必须分页；
- 批量操作需限制单批处理量（建议 ≤ 1000 条）；
- 耗时操作（如视频渲染、语音克隆调用）必须异步或使用任务队列，不阻塞主线程。

### 8.2 安全

- 对外服务使用 HTTPS 部署；
- 登录后接口必须有认证与授权控制（JWT/Session+权限）；
- 关键操作（生成视频、删除模板等）记录审计日志（操作者、时间、操作内容）。

---

## 9. 阿里巴巴 Java 开发手册核心约束（项目子集）

本项目在上述规范基础上，**参考《阿里巴巴 Java 开发手册》作为通用编码规范**，但只强制以下子集规则：

### 9.1 命名与编码风格

- 类、方法、变量命名须做到**见名知意**，禁止拼音缩写（业务专有名词除外）。
- 常量必须使用 `UPPER_SNAKE_CASE`，且全部放在常量类或枚举中，禁止魔法值散落代码。
- 集合与数组命名必须体现复数含义，如 `userList`、`templateMap`。
- 严禁使用 `l`、`O` 等易混淆字符作为变量名。

### 9.2 集合与空值处理

- 集合判空必须同时判 `null` 与 `isEmpty()`：
  - 推荐使用 `CollectionUtils.isEmpty(list)` / `MapUtils.isEmpty(map)` 等工具方法。
- 返回集合类型时，**不返回 `null`**，没有元素时返回空集合；
- 方法入参如果预期不为 `null`，应在入口进行校验并给出明确异常。

### 9.3 equals/hashCode 与字符串比较

- 覆写 `equals` 时必须同时覆写 `hashCode`；
- 字符串常量放在左边比较，避免 NPE：`"OK".equals(status)`；
- 禁止使用 `==` 比较字符串。

### 9.4 控制语句与异常

- `if/else` 分支不宜过多，如超过 3 层建议拆分为方法或使用策略/枚举等重构；
- `catch` 代码块不得为空，至少记录日志或转换为业务异常抛出；
- 禁止随意捕获顶层异常（如 `Exception`）后简单打印，必须有明确处理逻辑。

### 9.5 日志使用

- 必须使用 SLF4J 日志接口，不直接使用具体实现类；
- 日志输出使用占位符，不拼接字符串：`log.info("User {} login", userId);`；
- 日志级别使用规范：DEBUG 用于开发调试，INFO 用于业务关键流程，WARN/ERROR 用于异常与错误场景。

### 9.6 其他约束

- 禁止在循环中进行数据库或远程服务调用，必须预先合并为批量操作；
- 禁止在构造方法中做复杂逻辑（尤其是远程调用、IO 操作）；
- 工具类必须是无状态的，必要时使用 `final` + 私有构造函数避免被实例化。

---

## 10. 代码复用与禁止重复造轮子规范

### 10.1 总体原则

- **优先复用，禁止重复造轮子**：实现新功能前，必须先检查项目中是否已有相同或相似实现（工具类、组件、Service 方法、Hook 等）。
- 如能通过组合、封装或扩展现有代码实现需求，禁止重新写一套逻辑。
- 通用能力优先使用成熟库（JDK/Spring/Apache Commons/Hutool/axios/dayjs/lodash 等），不自行从零实现通用工具。

### 10.2 新功能开发前的复用性检查

- 开发前需完成简单的“复用性检查”，至少包括：
  - 在项目中搜索相关关键字（类名、方法名、业务名如 `videoTask`、`template` 等）。
  - 检查以下位置是否已有实现：`util`/`utils`、相关模块的 `service`/`repository`、前端 hooks、公用组件、统一 API 封装目录。
- 如发现已有类似实现：
  - 优先复用或在原实现基础上小幅扩展；
  - 若需重构，应先抽取公共部分为工具方法/组件，再在唯一位置维护逻辑。

### 10.3 工具类与公共组件

- 后端通用工具方法统一放在 `util` 等公共包中，禁止在业务类中新增“局部工具方法”再复制到其他类使用。
- 新增工具方法前必须检查是否能放入已有工具类（如字符串、集合、时间工具类）。
- 前端通用交互（列表+分页、常用弹框、表单布局等）应沉淀为公共组件或自定义 Hook，避免多个页面实现几乎相同的逻辑和结构。

### 10.4 第三方库引入约束

- 如现有库已能满足需求，**禁止**为解决同类问题再引入功能重复的新库（如多个日期处理库、多套 HTTP 库）。
- 引入新库前需确认：
  - 项目内无现有库可满足需求；
  - 新库稳定、社区活跃、无明显安全风险；
  - 已评估对包体积和学习成本的影响。

### 10.5 复制粘贴与逻辑唯一性

- 禁止跨类、跨模块复制粘贴大段业务逻辑：
  - 如多个地方出现高度相似逻辑，应抽取为公共方法/组件后复用；
- Bug 修复必须在逻辑的“唯一实现点”完成：
  - 如因历史复制导致逻辑分散，修复前需先合并/抽取，避免多处分别修改导致不一致。

### 10.6 AI 辅助开发的额外约束

- 使用 AI 生成代码时，必须明确告知：
  - 已存在的工具类、公共组件、API 封装目录，以及本节“禁止重复造轮子”的约束；
- 对与现有功能高度相似的需求，AI 应优先：
  - 复用现有类/方法/组件；
  - 或在原实现上小步扩展，而非从头实现新版本；
- 如 AI 建议新建通用工具类或引入新库，必须给出不能复用现有方案的理由以及新方案的适用边界。

---

## 11. 前端（React/Vue）编码规范摘要

### 11.1 目录与模块划分

- 采用按业务模块划分的目录结构，推荐：`api/`、`components/`、`features/` 或 `modules/`、`hooks/`、`store/`、`routes/`、`styles/`、`utils/`、`types/`。
- 业务模块目录内部可以包含页面、该模块专用子组件、API 封装和类型定义，避免所有代码堆在一个 `pages/` 目录下。

### 11.2 组件与状态

- 区分展示组件与容器组件：展示组件专注 UI，容器组件负责数据获取和业务逻辑。
- React 使用函数组件 + Hooks；Vue 使用组合式 API，尽量避免在单一组件中堆积过多职责。
- 优先使用局部状态（组件内部），仅将跨页面/跨模块的少量信息放入全局状态管理（如登录用户、全局配置）。

### 11.3 自定义 Hook / Composable

- 复杂数据流（如列表查询+分页+轮询）应封装为自定义 Hook（React）或 composable（Vue），避免复制粘贴逻辑。
- 自定义 Hook/Composable 文件与函数命名统一以 `use` 开头，例如 `useVideoTask`、`useTemplateList`。

### 11.4 API 调用与错误处理

- 统一在 `api/` 层封装 HTTP 客户端（如 axios 实例），所有业务 API 函数通过该实例调用后端接口。
- API 函数命名遵循 `getXxx/createXxx/updateXxx/deleteXxx`，返回统一结构（如 `{ code, message, data }`），前端对错误码集中处理。
- 网络错误与系统错误可在拦截器层统一处理（全局提示），业务错误由调用方根据场景决定提示方式。

### 11.5 样式与 UI

- 统一样式方案（如 CSS Modules、CSS-in-JS 或 Tailwind），避免在同一项目中混用多套完全不同的风格。
- 颜色、字体、间距等设计变量统一定义在主题文件或全局样式中，不在组件内部硬编码大量 magic number。

### 11.6 前端 Lint 与格式化

- 必须配置 ESLint + Prettier，新增前端代码需要无 ESLint 错误，提交前自动格式化。
- 建议使用 Husky + lint-staged 在 `git commit` 前自动执行 Lint 和格式化。

---

## 12. 部署架构规范（Windows 宿主机部署）

### 12.1 部署原则

- **宿主机环境**：Windows 10 系统，不使用 Docker 或 Kubernetes 等容器化方案。
- 服务进程管理：
  - Java 服务：Windows 服务或命令行启动；
  - Python Worker：Windows 服务、任务计划程序或命令行后台运行。
- 前端静态资源与后端服务由 **Nginx 统一接入**，Nginx 已接入其他业务系统，本项目通过路径规则区分。

### 12.2 整体部署架构

```
[外网请求] 
    ↓
[Nginx 接入层] (Windows 上运行，已接入其他业务系统)
    ├─ /{system}/{type}/*  # 两级路径路由规则
    │   ├─ /bless/web/*    → 祝福语系统前端静态资源
    │   ├─ /bless/api/*    → 祝福语系统 Java 后端 API
    │   └─ /其他系统/*      → 其他业务系统
    └─ 路由说明：
        - 第一级路径：系统标识(bless=祝福语系统)
        - 第二级路径：类型标识(web=前端, api=后端)
         ↓
    [Windows 宿主机进程]
    ├─ Spring Boot 后端 (jar 包，监听 127.0.0.1:8080)
    ├─ Python Worker (视频渲染服务，监听 127.0.0.1:5000)
    ├─ MySQL 8.0 (单实例)
    └─ Redis 4.x (当前单节点，未来主从部署)
```

### 12.3 前端部署

- **打包方式**：React 项目使用 `npm run build` 或 `yarn build` 打包生成静态文件到 `dist/` 目录。
- **部署方式**：将打包后的前端静态资源(HTML、CSS、JS、图片等)复制到 Windows 本地指定目录(如 `E:/www/bless/web/`)。
- **访问路径**：通过 Nginx 两级路由 `/bless/web/` 访问前端应用。

### 12.4 Nginx 配置要求（Windows 路径）

- 在现有 Nginx 配置中增加本项目的两级路由规则：
  - **第一级路径**：系统标识 `/bless/`（祝福语系统）
  - **第二级路径**：类型标识 `/web/`（前端）或 `/api/`（后端）

**路由设计说明**：
- 前端访问：`http://your-domain.com/bless/web/` → 静态资源
- 后端API：`http://your-domain.com/bless/api/` → Spring Boot 服务

示例配置片段（Windows 路径格式）：

```nginx
# 在现有 Nginx 配置文件(如 nginx.conf)中新增以下 location
server {
    listen 80;
    server_name your-domain.com;

    # 祝福语系统 - 前端静态资源 (两级路由: /bless/web/)
    location /bless/web/ {
        alias E:/www/bless/web/;  # Windows 路径，注意使用正斜杠
        try_files $uri $uri/ /bless/web/index.html;
        index index.html;
    }

    # 祝福语系统 - 后端 API (两级路由: /bless/api/)
    location /bless/api/ {
        proxy_pass http://127.0.0.1:8080/;  # 代理到本地 Spring Boot
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # 其他业务系统的 location 保持不变...
}
```

**Windows 环境注意事项**：
- Nginx 配置文件中的路径使用正斜杠 `/`，不要使用反斜杠 `\`；
- 示例：`E:/www/bless/web/` 而不是 `E:\www\bless\web\`；
- 确保 Nginx 进程有访问指定目录的权限。

### 12.5 后端 Spring Boot 部署（Windows）

- **打包方式**：使用 Maven/Gradle 打包为 **可执行 jar 包**。
- **启动方式**（Windows 环境）：
  - **方式1**：命令行启动（开发测试）
    ```cmd
    java -jar zhufuyu-movie.jar
    ```
  - **方式2**：批处理脚本后台启动
    ```cmd
    start /B java -jar zhufuyu-movie.jar > logs/app.log 2>&1
    ```
  - **方式3**：注册为 Windows 服务（生产推荐）
    - 使用工具如 [WinSW](https://github.com/winsw/winsw) 或 [NSSM](https://nssm.cc/)
    - 配置自动启动、日志管理、崩溃重启
  - **方式4**：Windows 任务计划程序
    - 设置开机自启动、定时检查进程存活

- **配置文件**：
  - 应用配置通过 `application.yml` 或 `application.properties` 管理；
  - 敏感配置（数据库密码、Redis 密码、第三方密钥等）建议通过环境变量或独立配置文件管理，不提交到版本库。
  
- **监听配置**：
  - 绑定到本地：`server.address=127.0.0.1`（仅允许 Nginx 代理访问）
  - 端口：`server.port=8080`
  - **context-path 配置**：不需要设置 `/bless/api`，Nginx 会剥离路径前缀后转发。

### 12.6 Python 渲染 Worker 部署与通信方式（Windows）

#### 12.6.1 部署方式

- **运行方式**：独立 Python 服务进程，监听本地端口提供 HTTP API（如 `http://127.0.0.1:5000`）。
  
- **Windows 部署方式**：
  - **方式1**：命令行启动（开发测试）
    ```cmd
    python worker.py
    ```
  - **方式2**：批处理脚本后台启动
    ```cmd
    start /B python worker.py > logs/worker.log 2>&1
    ```
  - **方式3**：注册为 Windows 服务（生产推荐）
    - 使用 [NSSM](https://nssm.cc/) 将 Python 脚本注册为服务
    - 配置自动启动、日志轮转、崩溃重启
  - **方式4**：Windows 任务计划程序
    - 设置开机自启动、定时健康检查

- **依赖管理**（Windows）：
  - 使用 `venv` 创建独立 Python 虚拟环境：
    ```cmd
    python -m venv venv
    venv\Scripts\activate
    pip install -r requirements.txt
    ```
  - 通过 `requirements.txt` 管理依赖（包括 MoviePy、FFmpeg-Python、Flask/FastAPI 等 Web 框架）。

- **FFmpeg 安装**（Windows）：
  - 下载 [FFmpeg Windows 版本](https://ffmpeg.org/download.html)；
  - 解压到指定目录（如 `E:/tools/ffmpeg/`）；
  - 将 `E:/tools/ffmpeg/bin` 添加到系统环境变量 PATH；
  - 验证安装：在 cmd 运行 `ffmpeg -version` 确认可用。

#### 12.6.2 通信方式选择

**方案A：HTTP 同步调用(推荐当前阶段)**

- Java 后端通过 HTTP 调用 Python 服务的 REST API(如 `http://127.0.0.1:5000/render`)；
- Python 使用 Flask/FastAPI 提供 HTTP 接口，接收渲染参数，返回渲染结果或任务ID；
- 适合快速实现，调试简单；
- 缺点：长时间渲染任务会占用 HTTP 连接，需考虑超时与异步化。

**方案B：消息队列异步解耦(未来演进)**

- Java 后端将渲染任务推送到 Redis 队列或 RabbitMQ；
- Python Worker 从队列中拉取任务并处理；
- 渲染完成后通过回调接口或更新数据库状态通知 Java；
- 适合高并发、大批量任务场景，解耦更彻底。

**当前建议**：
- 第1-2阶段使用**方案A(HTTP同步)**，简单直接；
- 第3-4阶段引入**方案B(消息队列)**，提升性能与稳定性。

### 12.7 MySQL 部署

- **当前阶段**：单实例部署，监听 `3306` 端口。
- 数据存储路径、字符集、时区配置需符合业务要求。
- 建议定期备份数据库（如每日凌晨自动导出 SQL）。

### 12.8 Redis 部署

- **当前阶段**：单节点部署，监听 `6379` 端口。
- **未来演进**：主从部署（Master-Slave），防止数据丢失。
  - Master 负责写入，Slave 负责备份与只读；
  - 建议启用 AOF 或 RDB 持久化机制。
- 密码配置：生产环境必须设置 Redis 访问密码，并与后端配置保持一致。

### 12.9 文件存储规范（Windows 路径）

#### 12.9.1 本地文件系统存储

- **当前阶段**：使用 Windows 本地文件系统存储上传的图片、音频、字体、生成的视频等。
- **存储根目录**：建议使用独立磁盘分区（如 `E:/data/bless/`），避免占用系统盘空间。
- **未来扩展**：可引入 MinIO(本地对象存储)或云存储(如阿里云 OSS)。

#### 12.9.2 目录结构规范

**两层目录设计原则（Windows 路径示例）**：

```
E:/data/bless/                  # 根目录（Windows 盘符路径）
├── uploads/                    # 第一级：业务路径(用户上传文件)
│   ├── images/                 # 第二级：业务定义的子目录(背景图片)
│   │   ├── 2025/01/            # 可选：按年月分层，避免单目录文件过多
│   │   └── 2025/02/
│   ├── audios/                 # 第二级：音频文件
│   │   ├── 2025/01/
│   │   └── 2025/02/
│   └── fonts/                  # 第二级：艺术字体
│       └── ttf/
├── videos/                     # 第一级：业务路径(生成的视频)
│   ├── finished/               # 第二级：已完成视频
│   │   ├── 2025/01/
│   │   └── 2025/02/
│   └── temp/                   # 第二级：临时渲染文件
├── templates/                  # 第一级：业务路径(模板素材)
│   ├── images/                 # 第二级：模板背景图
│   └── config/                 # 第二级：模板配置
└── logs/                       # 第一级：业务路径(日志文件)
    ├── render/                 # 第二级：渲染日志
    └── error/                  # 第二级：错误日志
```

**关键规范**：

1. **第一级目录**：按业务功能划分(uploads、videos、templates、logs等)，由系统架构决定。
2. **第二级目录**：由具体业务需求定义(如按文件类型、状态、时间分类)，避免单目录文件过多。
3. **时间分层**(可选)：对高频写入的目录(如 videos/finished)，可进一步按 `YYYY/MM/` 或 `YYYY/MM/DD/` 分层。
4. **文件命名**：使用唯一标识(UUID 或 雪花ID)+ 原始文件名或时间戳，避免重名冲突。
5. **Windows 路径处理**：
   - Java 代码中使用 `File.separator` 或 `Paths.get()` 自动适配路径分隔符；
   - 配置文件中路径使用正斜杠 `/` 或转义反斜杠 `\\`。

**Nginx 静态文件访问配置（Windows 路径）**：

```nginx
# 在 Nginx 配置中添加静态资源访问（两级路由）
location /bless/files/ {
    alias E:/data/bless/;  # Windows 路径，使用正斜杠
    # 仅允许访问 uploads 和 videos/finished
    # 禁止访问 temp、logs 等敏感目录
    autoindex off;  # 禁止目录浏览
}
```

### 12.10 监控与日志

- **日志管理**：
  - Spring Boot 日志：输出到文件，配置 Logback 按大小或日期轮转。
  - Python Worker 日志：通过 Python logging 输出到文件或 stdout，由进程管理器收集。
  - Nginx 访问日志与错误日志需定期清理或归档。
- **监控**：
  - 可选择使用轻量级监控工具（如 Prometheus + Grafana）或简单的进程存活检测脚本。
  - 关键指标：Java 进程 CPU/内存、Python Worker 进程状态、MySQL/Redis 连接数、磁盘空间。

### 12.11 环境划分

- **开发环境**：开发人员本地，使用内嵌 H2 或本地 MySQL/Redis。
- **测试环境**：单独服务器，与生产环境配置类似，用于功能测试和集成测试。
- **生产环境**：正式对外服务，必须有完善的备份、监控和安全防护。

---

本简化规范用于当前 zhufuyuMovie 项目，在保证**结构清晰、可维护性好**的前提下，刻意避免过度复杂的微服务和基础设施约束。后续如演进到 Spring Cloud 微服务，再在此基础上逐步补充服务拆分、注册中心、配置中心等规范。