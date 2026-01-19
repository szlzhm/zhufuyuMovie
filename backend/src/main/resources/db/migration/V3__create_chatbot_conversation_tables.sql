-- 迭代3: ChatBot对话历史功能 - 数据库初始化脚本

-- 1. 对话meta信息表
CREATE TABLE `chatbot_conversation_meta` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `conversation_id` VARCHAR(64) NOT NULL COMMENT '对话唯一标识(UUID v7生成的字符串)',
  `user_id` BIGINT NOT NULL COMMENT '用户ID(关联sys_user表)',
  `conversation_name` VARCHAR(50) NOT NULL COMMENT '对话名字(取第一次输入的前10个字符，不足时保留全部，全局唯一)',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_conversation_id` (`conversation_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ChatBot对话meta信息表';

-- 2. 对话详细内容表
CREATE TABLE `chatbot_conversation_detail` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `conversation_id` VARCHAR(64) NOT NULL COMMENT '对话ID(关联chatbot_conversation_meta表)',
  `detail_id` VARCHAR(64) NOT NULL COMMENT '详情唯一标识(UUID v7生成的字符串)',
  `role` TINYINT NOT NULL COMMENT '角色:0-用户提问,1-ChatBot回答',
  `content_type` VARCHAR(20) NOT NULL COMMENT '内容类型:text/image/audio/video/file',
  `content` TEXT COMMENT '对话内容(根据类型存储不同内容)',
  `original_filename` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名(用于文件类型内容)',
  `relative_path` VARCHAR(500) DEFAULT NULL COMMENT '相对路径文件名(用于文件类型内容)',
  `occurred_time` DATETIME NOT NULL COMMENT '发生时间',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_detail_id` (`detail_id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_occurred_time` (`occurred_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='ChatBot对话详细内容表';

-- 创建索引优化查询性能
CREATE INDEX idx_chatbot_conversation_meta_user_time ON chatbot_conversation_meta(user_id, created_time DESC);
CREATE INDEX idx_chatbot_conversation_detail_conv_time ON chatbot_conversation_detail(conversation_id, occurred_time ASC);