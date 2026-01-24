-- V4: Create Image Creation related tables

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示语模板表';

ALTER TABLE image_prompt_templates AUTO_INCREMENT = 1000;

CREATE TABLE image_prompts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '提示语ID',
    template_id BIGINT COMMENT '关联的模板ID',
    prompt_content TEXT NOT NULL COMMENT '完整的提示语文本',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示语表';

CREATE TABLE image_generation_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    prompt_id BIGINT NOT NULL COMMENT '关联的提示语ID',
    resolution VARCHAR(50) NOT NULL COMMENT '图像分辨率',
    num_images INT DEFAULT 1 COMMENT '生成图片数量',
    seed BIGINT DEFAULT -1 COMMENT '随机种子',
    smart_optimization TINYINT DEFAULT 0 COMMENT '是否开启智能优化：0-否，1-是',
    task_status VARCHAR(20) NOT NULL DEFAULT 'WAITING' COMMENT '任务状态：WAITING-等待中，PROCESSING-进行中，COMPLETED-已完成，FAILED-失败，CANCELED-取消，PAUSED-暂停',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '任务更新时间',
    status_changed_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '状态变更时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图像生成任务表';

CREATE TABLE image_generation_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '结果ID',
    prompt_id BIGINT NOT NULL COMMENT '关联的提示语ID',
    task_id BIGINT NOT NULL COMMENT '关联的任务ID',
    image_path VARCHAR(500) NOT NULL COMMENT '生成的图片文件路径',
    image_url VARCHAR(500) COMMENT '图片访问URL',
    image_id VARCHAR(100) UNIQUE COMMENT '图片唯一ID（通常由雪花算法或其他全局ID生成器生成）',
    completed_time DATETIME COMMENT '完成时间',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图像生成结果表';
