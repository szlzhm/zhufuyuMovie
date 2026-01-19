-- 迭代2: 系统配置模块 - 数据库初始化脚本

-- 1. 系统配置表
CREATE TABLE `sys_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键(唯一)',
  `config_value` TEXT NOT NULL COMMENT '配置值',
  `config_desc` VARCHAR(255) DEFAULT NULL COMMENT '配置说明',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 初始化数据根目录配置
INSERT INTO `sys_config` (`config_key`, `config_value`, `config_desc`) 
VALUES ('file.root.path', 'E:/data/bless/', '文件存储根目录');

-- 2. 情绪枚举表
CREATE TABLE `sys_emotion` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `emotion_code` VARCHAR(50) NOT NULL COMMENT '情绪编码(唯一,如CHEERFUL)',
  `emotion_name` VARCHAR(50) NOT NULL COMMENT '显示名称(如喜庆)',
  `usage_desc` VARCHAR(500) DEFAULT NULL COMMENT '用途说明',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1启用,0禁用',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_emotion_code` (`emotion_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情绪枚举表';

-- 初始化情绪数据
INSERT INTO `sys_emotion` (`emotion_code`, `emotion_name`, `usage_desc`, `status`) VALUES
('CHEERFUL', '喜庆', '适合春节、婚礼、庆典等欢乐场景,氛围热闹、喜气洋洋', 1),
('WARM', '温馨', '适合家庭、亲子、朋友、感恩等温暖场景,氛围柔和亲切', 1),
('TOUCHING', '感动', '适合告白、回忆、感谢、纪念等真情流露的场景', 1),
('INSPIRING', '励志', '适合鼓励、打气、升学、升职等积极向上的场景', 1),
('SOOTHING', '舒缓', '适合晚安、治愈、宁静、放松等慢节奏场景', 1),
('LIVELY', '活泼', '适合儿童、日常搞怪、轻松幽默的场景,节奏轻快', 1),
('SOLEMN', '庄重', '适合正式祝贺、长辈祝福、纪念类等较为严肃的场景', 1),
('DAILY', '日常', '适合不强调明显情绪的普通祝福场景,氛围中性', 1);

-- 3. 图片分类表
CREATE TABLE `sys_image_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父分类ID,NULL表示一级分类',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1启用,0禁用',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片分类表';

-- 初始化图片分类数据(一级分类)
INSERT INTO `sys_image_category` (`category_name`, `parent_id`, `sort_order`, `status`) VALUES
('场景', NULL, 1, 1),
('节日', NULL, 2, 1),
('情感', NULL, 3, 1);

-- 初始化图片分类数据(二级分类 - 场景)
INSERT INTO `sys_image_category` (`category_name`, `parent_id`, `sort_order`, `status`) VALUES
('生日', (SELECT id FROM (SELECT id FROM sys_image_category WHERE category_name='场景' AND parent_id IS NULL) AS t), 1, 1),
('婚礼', (SELECT id FROM (SELECT id FROM sys_image_category WHERE category_name='场景' AND parent_id IS NULL) AS t), 2, 1),
('商务', (SELECT id FROM (SELECT id FROM sys_image_category WHERE category_name='场景' AND parent_id IS NULL) AS t), 3, 1);

-- 初始化图片分类数据(二级分类 - 节日)
INSERT INTO `sys_image_category` (`category_name`, `parent_id`, `sort_order`, `status`) VALUES
('春节', (SELECT id FROM (SELECT id FROM sys_image_category WHERE category_name='节日' AND parent_id IS NULL) AS t), 1, 1),
('中秋', (SELECT id FROM (SELECT id FROM sys_image_category WHERE category_name='节日' AND parent_id IS NULL) AS t), 2, 1),
('国庆', (SELECT id FROM (SELECT id FROM sys_image_category WHERE category_name='节日' AND parent_id IS NULL) AS t), 3, 1);

-- 初始化图片分类数据(二级分类 - 情感)
INSERT INTO `sys_image_category` (`category_name`, `parent_id`, `sort_order`, `status`) VALUES
('温馨', (SELECT id FROM (SELECT id FROM sys_image_category WHERE category_name='情感' AND parent_id IS NULL) AS t), 1, 1),
('励志', (SELECT id FROM (SELECT id FROM sys_image_category WHERE category_name='情感' AND parent_id IS NULL) AS t), 2, 1),
('感恩', (SELECT id FROM (SELECT id FROM sys_image_category WHERE category_name='情感' AND parent_id IS NULL) AS t), 3, 1);

-- 4. 文案分类表
CREATE TABLE `sys_text_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父分类ID,NULL表示一级分类',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1启用,0禁用',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文案分类表';

-- 初始化文案分类数据(一级分类)
INSERT INTO `sys_text_category` (`category_name`, `parent_id`, `sort_order`, `status`) VALUES
('祝福类型', NULL, 1, 1),
('使用场景', NULL, 2, 1),
('节日主题', NULL, 3, 1);

-- 初始化文案分类数据(二级分类 - 祝福类型)
INSERT INTO `sys_text_category` (`category_name`, `parent_id`, `sort_order`, `status`) VALUES
('生日祝福', (SELECT id FROM (SELECT id FROM sys_text_category WHERE category_name='祝福类型' AND parent_id IS NULL) AS t), 1, 1),
('节日祝福', (SELECT id FROM (SELECT id FROM sys_text_category WHERE category_name='祝福类型' AND parent_id IS NULL) AS t), 2, 1),
('日常问候', (SELECT id FROM (SELECT id FROM sys_text_category WHERE category_name='祝福类型' AND parent_id IS NULL) AS t), 3, 1);

-- 初始化文案分类数据(二级分类 - 使用场景)
INSERT INTO `sys_text_category` (`category_name`, `parent_id`, `sort_order`, `status`) VALUES
('朋友', (SELECT id FROM (SELECT id FROM sys_text_category WHERE category_name='使用场景' AND parent_id IS NULL) AS t), 1, 1),
('家人', (SELECT id FROM (SELECT id FROM sys_text_category WHERE category_name='使用场景' AND parent_id IS NULL) AS t), 2, 1),
('同事', (SELECT id FROM (SELECT id FROM sys_text_category WHERE category_name='使用场景' AND parent_id IS NULL) AS t), 3, 1);

-- 初始化文案分类数据(二级分类 - 节日主题)
INSERT INTO `sys_text_category` (`category_name`, `parent_id`, `sort_order`, `status`) VALUES
('春节', (SELECT id FROM (SELECT id FROM sys_text_category WHERE category_name='节日主题' AND parent_id IS NULL) AS t), 1, 1),
('中秋', (SELECT id FROM (SELECT id FROM sys_text_category WHERE category_name='节日主题' AND parent_id IS NULL) AS t), 2, 1),
('元旦', (SELECT id FROM (SELECT id FROM sys_text_category WHERE category_name='节日主题' AND parent_id IS NULL) AS t), 3, 1);
