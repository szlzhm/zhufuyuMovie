# 图片创作功能需求分析与设计方案

## 1. 功能需求概述

### 1.1 菜单结构
- 新增顶级菜单：图片创作
- 子菜单包括：
  - 创作提示语提交
  - 创作任务管理
  - 创作结果列表
  - 提示语模板管理

### 1.2 提示语提交功能
- 文本填写框：占用工作区一半空间
- 提交按钮
- 分辨率选择下拉框（Qwen-Image2512支持的多种分辨率）
- 随机种子滑块（-1到99999999）
- 智能优化复选框
- 生成图片数量输入框（支持上下箭头调整）
- "从提示语模板导入"按钮

### 1.3 模板管理功能
- 模板为5000字符内的纯文本，使用{{VARIABLE_NAME}}格式的占位符
- 包含模板内容、创建日期、占位符、状态等信息
- 支持启用/禁用/删除操作
- 分页展示，支持搜索和过滤

### 1.4 任务管理功能
- 任务状态：等待中、进行中、已完成、失败、取消、暂停
- 分页展示，支持按状态、时间、提示语进行过滤
- 支持删除、启用、暂停等操作

### 1.5 结果管理功能
- 展示生成的图片、使用的提示语、模板、耗时等信息
- 支持批量下载
- 支持按提示语、时间进行过滤

## 2. 数据库设计

### 2.1 提示语模板表 (image_prompt_templates)
```sql
CREATE TABLE image_prompt_templates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板ID，从1000开始',
    template_content VARCHAR(5000) NOT NULL COMMENT '模板完整文本内容',
    placeholder_keywords VARCHAR(500) COMMENT '占位符关键字，JSON格式存储多个关键字',
    template_status TINYINT DEFAULT 1 COMMENT '模板状态：1-启用，0-禁用',
    is_deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    deleted_time DATETIME NULL COMMENT '删除时间，仅当is_deleted=1时有效',
    status_changed_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '状态变更时间',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间，用于运维'
);
```

### 2.2 提示语表 (image_prompts)
```sql
CREATE TABLE image_prompts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '提示语ID',
    template_id BIGINT COMMENT '关联的模板ID',
    prompt_content TEXT NOT NULL COMMENT '完整的提示语文本',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

### 2.3 任务表 (image_generation_tasks)
```sql
CREATE TABLE image_generation_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    prompt_id BIGINT NOT NULL COMMENT '关联的提示语ID',
    resolution VARCHAR(20) NOT NULL COMMENT '图像分辨率',
    num_images INT DEFAULT 1 COMMENT '生成图片数量',
    seed BIGINT DEFAULT -1 COMMENT '随机种子',
    smart_optimization TINYINT DEFAULT 0 COMMENT '是否开启智能优化：0-否，1-是',
    task_status VARCHAR(20) NOT NULL DEFAULT 'WAITING' COMMENT '任务状态：WAITING-等待中，PROCESSING-进行中，COMPLETED-已完成，FAILED-失败，CANCELED-取消，PAUSED-暂停',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务更新时间',
    status_changed_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '状态变更时间'
);
```

### 2.4 结果表 (image_generation_results)
```sql
CREATE TABLE image_generation_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '结果ID',
    prompt_id BIGINT NOT NULL COMMENT '关联的提示语ID',
    task_id BIGINT NOT NULL COMMENT '关联的任务ID',
    image_path VARCHAR(500) NOT NULL COMMENT '生成的图片文件路径',
    image_url VARCHAR(500) COMMENT '图片访问URL',
    image_id VARCHAR(100) UNIQUE COMMENT '图片唯一ID（雪花算法生成）',
    completed_time DATETIME COMMENT '完成时间',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

## 3. 后端实现方案

### 3.1 实体类 (Entities)
- ImagePromptTemplateEntity - 提示语模板实体
- ImagePromptEntity - 提示语实体
- ImageGenerationTaskEntity - 任务实体
- ImageGenerationResultEntity - 结果实体

### 3.2 Repository层
- ImagePromptTemplateRepository - 模板数据访问
- ImagePromptRepository - 提示语数据访问
- ImageGenerationTaskRepository - 任务数据访问
- ImageGenerationResultRepository - 结果数据访问

### 3.3 Service层
- ImagePromptTemplateService - 模板管理服务
- ImagePromptService - 提示语管理服务
- ImageGenerationTaskService - 任务管理服务
- ImageGenerationResultService - 结果管理服务

### 3.4 Controller层
- ImagePromptTemplateController - 模板管理接口
- ImagePromptSubmitController - 提示语提交接口
- ImageGenerationTaskController - 任务管理接口
- ImageGenerationResultController - 结果管理接口
- ImageUploadCallbackController - 图像生成回调接口

## 4. 前端实现方案

### 4.1 菜单结构
在 MainLayout.jsx 中添加新的菜单项

### 4.2 页面组件
- ImageCreationPromptSubmitPage - 提示语提交页面
- ImageCreationTaskManagementPage - 任务管理页面
- ImageCreationResultListPage - 结果列表页面
- ImageCreationTemplateManagementPage - 模板管理页面

### 4.3 公共组件
- ImageCreationTemplateModal - 模板选择模态框
- ImagePreviewModal - 图片预览模态框
- ImageDownloadProgress - 图片下载进度组件

## 5. 技术细节

### 5.1 模板占位符格式
使用 {{VARIABLE_NAME}} 格式作为占位符，例如：
```
一只{{CATEGORY}}风格的{{FLOWERING_PLANT}}花，背景是{{BACKGROUND_STYLE}}
```

### 5.2 分辨率选项
支持以下分辨率选项：
- "1:1 (1024x1024)": 1024x1024
- "9:16 (832x1536)": 832x1536
- "16:9 (1536x832)": 1536x832
- "4:3 (1216x912)": 1216x912
- "3:4 (912x1216)": 912x1216
- "9:16 (684x1216)": 684x1216
- "21:9 (1920x832)": 1920x832
- "1:1 (512x512)": 512x512
- "9:16 (416x768)": 416x768
- "16:9 (768x416)": 768x416
- "4:3 (608x456)": 608x456
- "3:4 (456x608)": 456x608
- "9:16 (342x608)": 342x608
- "21:9 (960x416)": 960x416

### 5.3 雪花算法实现
使用雪花算法生成图片ID，确保全局唯一性。

### 5.4 文件命名
生成的图片文件名由图像生成服务提供，保证不重复。

## 6. 权限控制
所有接口都需要验证用户身份，并根据用户角色进行权限控制。

## 7. 现有组件复用
从前端代码中可以看到，项目已经具备了以下可复用的组件：
- 分页组件 (在 VideoTaskPage.jsx 中)
- 模态框组件 (在 VideoTaskPage.jsx 中)
- 表格组件 (在 VideoTaskPage.jsx 中)
- 搜索和筛选功能 (在 VideoTaskPage.jsx 中)
- 图片预览功能 (在 TextToImagePage.jsx 中)
- 下载进度条 (在 TextToImagePage.jsx 中)

## 8. 特殊功能需求

### 8.1 悬浮提示功能
- 鼠标悬停在提示语上5秒后，显示完整的提示语
- 鼠标悬停在图片上5秒后，显示图片原始大图，且可下载

### 8.2 批量下载功能
- 支持批量下载选中的图片结果
- 使用自定义下载进度条而非浏览器默认进度条

### 8.3 模板导入功能
- 用户可以从模板导入提示语
- 替换模板中的占位符为实际内容
- 支持模板搜索和分页浏览

这套设计方案充分利用了现有的架构和组件，确保与项目整体风格保持一致。