-- V7: Add placeholder_keywords column if not exists

-- 检查并添加 placeholder_keywords 字段（如果不存在）
ALTER TABLE image_prompt_templates 
ADD COLUMN IF NOT EXISTS placeholder_keywords VARCHAR(500) COMMENT '占位符关键字，多个以逗号分隔'
AFTER template_content;

-- 检查并添加 template_parameters 字段（如果不存在）
ALTER TABLE image_prompt_templates 
ADD COLUMN IF NOT EXISTS template_parameters TEXT COMMENT '模板预设参数，JSON格式'
AFTER template_status;

-- 检查并添加 template_image_path 字段（如果不存在）
ALTER TABLE image_prompt_templates 
ADD COLUMN IF NOT EXISTS template_image_path VARCHAR(500) COMMENT '模板图片文件路径'
AFTER template_parameters;

-- 检查并添加 template_image_url 字段（如果不存在）
ALTER TABLE image_prompt_templates 
ADD COLUMN IF NOT EXISTS template_image_url VARCHAR(500) COMMENT '模板图片访问URL'
AFTER template_image_path;
