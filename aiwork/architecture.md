## 祝福语视频系统整体架构说明

### 1. 目标概述

本系统面向不擅长使用剪映等专业工具的普通用户，提供一个"傻瓜式祝福视频工厂"能力：
- 通过**预置视频模板 + 祝福语文案库 + 用户语音克隆**，一键生成个性化祝福视频。
- 支持运营侧/企业侧批量生成、管理和下载祝福视频。

整体采用：**前端 Web + Java 业务后台 + Python 渲染 Worker + 外部语音克隆/TTS 服务** 的分层架构。

**部署环境说明**：
- 宿主机环境：Windows 10
- 接入层：Nginx（两级路由规则：`/{system}/{type}/`）
  - 第一级：系统标识（`bless` = 祝福语系统）
  - 第二级：类型标识（`web` = 前端，`api` = 后端API）
- 访问路径示例：
  - 前端：`http://your-domain.com/bless/web/`
  - 后端API：`http://your-domain.com/bless/api/`

---

### 2. 整体技术架构

```mermaid
graph TD
  subgraph Client[前端客户端]
    U1[普通用户前台]
    U2[运营管理后台]
  end
  
  subgraph Nginx[Nginx 接入层 - Windows]
    N1[/bless/web/ → 前端静态资源]
    N2[/bless/api/ → Java后端]
    N3[两级路由规则说明]
  end

  subgraph Java[Java 业务后台]
    J1[用户与权限管理]
    J2[素材管理:图片/字体/音频]
    J3[模板与祝福语文本库管理]
    J4[视频任务与批量任务管理]
    J5[语音克隆/TTS 接入封装]
    J6[REST API 网关]
  end

  subgraph Queue[消息队列/任务队列]
    Q1[Render Task Queue]
  end

  subgraph Py[Python 渲染 Worker - Windows]
    P1[任务消费与调度]
    P2[模板 JSON 解析]
    P3[MoviePy/FFmpeg 渲染引擎]
    P4[结果回写与通知]
  end

  subgraph Store[存储层 - Windows]
    DB[(MySQL 数据库)]
    FS[(本地文件存储: E:/data/bless/)]
  end

  subgraph Voice[外部语音服务]
    V1[语音克隆服务]
    V2[TTS 文本转语音]
  end

  U1 -->|HTTP/HTTPS| N1
  U2 -->|HTTP/HTTPS| N1
  U1 -->|HTTP/HTTPS| N2
  U2 -->|HTTP/HTTPS| N2
  
  N1 -.->|静态文件| Client
  N2 -->|proxy_pass| J6

  J6 --> J1
  J6 --> J2
  J6 --> J3
  J6 --> J4
  J6 --> J5

  J1 --> DB
  J2 --> DB
  J3 --> DB
  J4 --> DB
  J2 --> FS
  J4 --> FS

  J4 -->|创建渲染任务| Q1
  P1 -->|消费任务| Q1
  P1 --> P2 --> P3
  P3 --> FS
  P4 --> DB

  J5 --> V1
  J5 --> V2

  J5 -->|语音样本/文本| Voice
  Voice -->|voiceId/音频URL| J5
```

---

### 3. 核心组件说明

- **前端客户端**
  - **普通用户前台**：
    - 场景与模板选择（生日/春节/婚礼等）。
    - 祝福语文本选择与编辑。
    - 语音采集（首次克隆）、生成祝福语音频、一键生成视频、预览与下载。
  - **运营管理后台**：
    - 素材管理：背景图片、艺术字体、背景音乐/祝福语音频等。
    - 模板管理：定义模板元数据与模板 JSON（特效配置）。
    - 祝福语文本库管理：按场景维护推荐祝福语。
    - 视频任务与批量任务监控、失败重试、结果下载。

- **Java 业务后台**
  - **用户与权限管理（J1）**：注册登录、角色与权限控制（普通用户 vs 运营/管理员）。
  - **素材管理（J2）**：
    - 背景图片、音频、字体等素材的上传、元数据维护与启停。
    - 与对象存储 FS 对接，生成可访问 URL。
  - **模板与文本库管理（J3）**：
    - 维护 `video_template`（模板 JSON）、`blessing_text`（祝福语文本库）。
  - **视频任务与批量任务管理（J4）**：
    - 单个/批量视频生成任务的创建、状态流转（PENDING/PROCESSING/SUCCESS/FAIL）。
    - 将渲染任务写入队列 Q1，消费结果回写 DB，并提供查询接口给前端。\
  - **语音克隆/TTS 接入封装（J5）**：
    - 封装第三方 V1/V2 服务，提供统一接口：
      - `registerVoice(userId, samples...) -> voiceId`。
      - `synthesizeVoice(userId, voiceId, text) -> audioUrl`。
    - 维护 `user_voice_profile` 等数据表，记录用户 voiceId 与使用情况。
  - **REST API 网关（J6）**：
    - 对前端暴露统一 HTTP API。
    - 对 Python Worker 暴露任务结果回调/查询接口。

- **消息队列 / 任务队列（Q1）**
  - 解耦“任务创建”和“视频渲染”，实现异步批量处理。
  - 支持控制并发、失败重试和多 Worker 扩容。

- **Python 渲染 Worker**
  - **任务消费与调度（P1）**：从 Q1 拉取任务，解析任务 JSON。
  - **模板 JSON 解析（P2）**：
    - 解析模板 JSON（背景、字体、特效参数、轨迹、缩放、残影等）与运行参数（祝福语文本、音频 URL 等）。
  - **MoviePy/FFmpeg 渲染引擎（P3）**：
    - 使用 MoviePy 组合 ImageClip/TextClip/CompositeVideoClip，调用 FFmpeg 完成编码。
    - 支持：静态图 + 音频、文字飞入/缩放/残影、后续扩展粒子/滤镜等。
  - **结果回写与通知（P4）**：
    - 渲染完成后上传视频到 FS，得到 `video_url`。
    - 调用 Java 后端回调接口更新任务状态与结果。

- **存储层（DB + FS）**
  - **DB**：
    - 用户、素材（图片/字体/音频）、模板、祝福语文本库、用户语音配置、视频任务/批量任务、生成结果记录等。
  - **FS**：
    - 大文件存储：背景图片、字体文件、原始语音录音、语音克隆生成的音频、最终视频文件。

- **外部语音服务（Voice）**
  - **语音克隆服务（V1）**：
    - 接收用户语音样本，返回可用于 TTS 的 `voiceId`。
  - **TTS 文本转语音服务（V2）**：
    - 接收 `voiceId + 文本`，返回合成后的祝福语音频 URL 或二进制数据。

---

### 4. 与四阶段实现路线的关系简述

- **阶段一：MVP**
  - 使用 Java + Python Worker + FFmpeg 实现“静态图 + 现有音频 → 视频”，打通素材管理、任务队列、渲染与下载的最小闭环。
- **阶段二：模板 + 轻量特效**
  - 引入模板 JSON 与 MoviePy 渲染，引入文字飞入、缩放、残影等特效，由运营侧维护模板，用户仅选择模板与填文字。
- **阶段三：个性化祝福（文本库 + 语音克隆）**
  - 在当前架构上接入外部语音克隆/TTS 服务，新增祝福语文本库和用户语音克隆流程，让普通用户通过前台一键生成“自己声音”的个性化祝福视频。
- **阶段四：高级特效与扩展**
  - 在已有架构上扩展更多 MoviePy/FFmpeg 高级特效，或接入粒子/AI 等增强能力，同时通过多 Worker、监控与优化提升并发与稳定性。

本文件用于指导后续详细设计（API 设计、数据表结构、模板 JSON 规范等），在实现时可以以此为蓝本逐步细化。